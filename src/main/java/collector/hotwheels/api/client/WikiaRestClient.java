package collector.hotwheels.api.client;

import collector.hotwheels.api.model.AllCategories;
import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.api.model.CategoryMembers;
import collector.hotwheels.api.model.Query;
import collector.hotwheels.api.model.QueryResponse;
import collector.hotwheels.api.model.RevisionContent;
import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.util.rest.EndPointSettings;
import collector.hotwheels.insight.util.rest.RestClient;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static collector.hotwheels.api.client.WikiaContentParser.GetImageFilename_fromUrl;
import static collector.hotwheels.insight.util.StringUtils.URIDecode;
import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class WikiaRestClient extends RestClient {
    // TODO: https://hotwheels.fandom.com/api.php?format=json&action=imageserving&wisId=5331
    // https://hotwheels.fandom.com/api.php?format=json&action=query&prop=imageinfo&iiprop=url&titles=File:2248.JPG

    private final Logger logger = LoggerFactory.getLogger(WikiaRestClient.class);
    private final String downloadPath;
    private final int DEFAULT_LIMIT = 500;
    private int requestLimit;

    protected WikiaRestClient(String downloadPath, EndPointSettings endPointSettings, int requestLimit) {
        super(endPointSettings);
        this.requestLimit = requestLimit;
        this.downloadPath = downloadPath;

    }

    protected <T> List<T> getListOfItemsFromJSON(Class clazz) {
        List<T> ret = null;

        try {
            String jsonContent = getResponseContent();

            if (!isNullOrEmpty(jsonContent)) {
                ObjectMapper objectMapper = new ObjectMapper();

                JavaType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, clazz);

                if (jsonContent.startsWith("[")) {
                    ret = objectMapper.readValue(jsonContent, listType);
                } else {
                    T singleObject = (T) objectMapper.readValue(jsonContent, clazz);

                    if (singleObject != null) {
                        ret = new ArrayList<>();
                        ret.add(singleObject);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting List of items: " + clazz.getName(), ex);
        }

        return ret;
    }

    private static final String QUERY_API_VALID = "/api.php?format=json&action=query&prop=info";

    protected boolean IsValid() {
        setEndPointPath(QUERY_API_VALID);
        String response = getResponseContent();
        return (!isNullOrEmpty(response) && "[]".equals(response));
    }

    private static final String QUERY_ALL_CATEGORIES = "/api.php?format=json&action=query&list=allcategories&acprop=id";

    protected Set<BaseItem> GetAllCategoryList() {
        Set<BaseItem> categoryList = new HashSet<>();

        try {
            setEndPointPath(QUERY_ALL_CATEGORIES);
            String offset = null;
            List<QueryResponse> response;

            do {
                setAllCategoryParams(DEFAULT_LIMIT, offset);
                response = getListOfItemsFromJSON(QueryResponse.class);

                if (response != null && !response.isEmpty()) {
                    Query query = getQueryFromResult(response);

                    if (query != null && query.getAllcategories() != null && !query.getAllcategories()
                            .isEmpty()) {
                        for (AllCategories categoryEntry : query.getAllcategories()) {
                            if (categoryEntry != null && categoryEntry.getAdditionalProperties() != null &&
                                    !categoryEntry.getAdditionalProperties()
                                            .isEmpty()) {
                                BaseItem baseItem =
                                        build_valid_BaseItem(categoryEntry.getAdditionalProperties(), "pageid", "*");

                                if (baseItem != null) {
                                    categoryList.add(baseItem);
                                }
                                if (requestLimit > 0 && categoryList.size() >= requestLimit) {
                                    return categoryList;
                                }
                            }
                        }
                    }
                    offset = getQueryContinueValue(response);
                } else {
                    offset = null;
                }
            } while (!isNullOrEmpty(offset));

        } catch (Exception ex) {
            logger.error("Error getting List of categories", ex);
        }

        return categoryList;
    }

    private void setAllCategoryParams(int limit, String offset) {
        String paramString = "&aclimit=" + limit;

        if (!isNullOrEmpty(offset)) {
            paramString += "&acfrom=" + offset;
        }
        setParams(paramString);
    }

    private static final String QUERY_CATEGORY_MEMBER =
            "/api.php?format=json&action=query&list=categorymembers&cmnamespace=0";

    protected Set<BaseItem> GetCategoryMember(String pageId) {
        Set<BaseItem> categoryMemberList = new HashSet<>();

        try {
            setEndPointPath(QUERY_CATEGORY_MEMBER);
            String offset = null;
            List<QueryResponse> response;

            do {
                setCategoryMemberParams(pageId, DEFAULT_LIMIT, offset);
                response = getListOfItemsFromJSON(QueryResponse.class);

                if (response != null && !response.isEmpty()) {
                    Query query = getQueryFromResult(response);

                    if (query != null && query.getCategorymembers() != null && !query.getCategorymembers()
                            .isEmpty()) {
                        for (CategoryMembers categoryMember : query.getCategorymembers()) {
                            if (categoryMember != null && categoryMember.getAdditionalProperties() != null &&
                                    !categoryMember.getAdditionalProperties()
                                            .isEmpty()) {
                                BaseItem baseItem =
                                        build_valid_BaseItem(categoryMember.getAdditionalProperties(), "pageid",
                                                "title");

                                if (baseItem != null) {
                                    categoryMemberList.add(baseItem);
                                }
                                if (requestLimit > 0 && categoryMemberList.size() >= requestLimit) {
                                    return categoryMemberList;
                                }
                            }
                        }
                    }
                    offset = getQueryContinueValue(response);
                } else {
                    offset = null;
                }
            } while (!isNullOrEmpty(offset));

        } catch (Exception ex) {
            logger.error("Error getting List of categories", ex);
        }

        return categoryMemberList;
    }

    private void setCategoryMemberParams(String pageId, int limit, String offset) throws Exception {

        String paramString = "&cmlimit=" + limit;

        if (isNullOrEmpty(pageId)) {
            throw new Exception("Page Id must be defined!");
        } else {
            paramString += "&cmpageid=" + pageId;
        }

        if (!isNullOrEmpty(offset)) {
            paramString += "&cmcontinue=" + offset;
        }
        setParams(paramString);
    }

    private static final String QUERY_REVISION_CONTENT =
            "/api.php?format=json&action=query&prop=revisions&rvprop=content";

    protected Set<RevisionContent> GetRevisionContentList(String pageIds) {
        Set<RevisionContent> revisionContentList = new HashSet<>();

        try {
            setEndPointPath(QUERY_REVISION_CONTENT);
            setListRevisionContentParams(pageIds);

            Query query = getQueryFromResult(getListOfItemsFromJSON(QueryResponse.class));

            if (query != null && query.getPages() != null && query.getPages()
                    .getAdditionalProperties() != null && !query.getPages()
                    .getAdditionalProperties()
                    .isEmpty()) {
                try {

                    query.getPages()
                            .getAdditionalProperties()
                            .forEach((k, v) -> {
                                try {
                                    List<LinkedHashMap> revisions =
                                            (List<LinkedHashMap>) ((LinkedHashMap) v).get("revisions");
                                    if (revisions != null && !revisions.isEmpty() && !revisions.get(0)
                                            .isEmpty() && !isNullOrEmpty(revisions.get(0)
                                            .get("*")
                                            .toString())) {
                                        RevisionContent revisionContent = new RevisionContent();
                                        revisionContent.setContent(revisions.get(0)
                                                .get("*")
                                                .toString());
                                        revisionContent.setPageid(k);
                                        revisionContent.setTitle((String) ((LinkedHashMap) v).get("title"));

                                        revisionContentList.add(revisionContent);
                                    }
                                } catch (Exception ee) {
                                    logger.error("Could not get RevisionContent for: " + k, ee);
                                }
                            });

                } catch (Exception qe) {
                    logger.error("Error getting query response data ", qe);
                }

            }
        } catch (Exception ex) {
            logger.error("Error getting List of RevisionContent for: " + pageIds, ex);
        }

        return revisionContentList;
    }

    private void setListRevisionContentParams(String pageIds) throws Exception {
        if (isNullOrEmpty(pageIds)) {
            throw new Exception("pageids definition is missing!");
        } else if (pageIds.endsWith("|")) {
            pageIds = pageIds.substring(0, pageIds.length() - 1);
        }

        String paramString = "&pageids=" + pageIds;

        setParams(paramString);
    }

    private static final String QUERY_PAGE_IMG = "/api.php?format=json&action=imageserving&wisId={0}";

    private String getImageUrl_forPage(String pageId) {
        String imgUrl = null;

        try {
            if (!isNullOrEmpty(pageId)) {
                setParams(null);
                setEndPointPath(QUERY_PAGE_IMG, pageId);
                List<QueryResponse> response = getListOfItemsFromJSON(QueryResponse.class);

                if (response != null && !response.isEmpty() && response.get(0)
                        .getImage() != null && !isNullOrEmpty(response.get(0)
                        .getImage()
                        .getImageserving())) {
                    imgUrl = response.get(0)
                            .getImage()
                            .getImageserving();
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting image url for: " + pageId);
        }
        return imgUrl;
    }

    private static final String QUERY_FILE_IMG =
            "/api.php?format=json&action=query&prop=imageinfo&iiprop=url&titles=File:{0}";

    private String getImageUrl_forFile(String fileName) {
        final String[] imgUrl = new String[1];

        try {
            if (!isNullOrEmpty(fileName)) {
                setParams(null);
                setEndPointPath(QUERY_FILE_IMG, fileName);
                List<QueryResponse> response = getListOfItemsFromJSON(QueryResponse.class);

                Query query = getQueryFromResult(response);

                if (query != null && query.getPages() != null && query.getPages()
                        .getAdditionalProperties() != null && !query.getPages()
                        .getAdditionalProperties()
                        .isEmpty()) {

                    query.getPages()
                            .getAdditionalProperties()
                            .forEach((k, v) -> {
                                try {
                                    List<LinkedHashMap> imageinfos =
                                            (List<LinkedHashMap>) ((LinkedHashMap) v).get("imageinfo");
                                    if (imageinfos != null && !imageinfos.isEmpty() && !imageinfos.get(0)
                                            .isEmpty() && !isNullOrEmpty(imageinfos.get(0)
                                            .get("url")
                                            .toString())) {
                                        imgUrl[0] = imageinfos.get(0)
                                                .get("url")
                                                .toString();
                                    }
                                } catch (Exception ee) {
                                    logger.error("Could not get imageinfo for: " + k, ee);
                                }
                            });

                }
            }
        } catch (Exception ex) {
            logger.error("Error getting image url for: " + fileName);
        }
        return imgUrl[0];
    }

    private Query getQueryFromResult(List<QueryResponse> response) {
        if (response != null && !response.isEmpty() && response.get(0)
                .getQuery() != null) {
            return response.get(0)
                    .getQuery();
        } else {
            return null;
        }
    }

    private String getQueryContinueValue(List<QueryResponse> response) {
        if (response != null && !response.isEmpty() && response.get(0)
                .getQueryContinue() != null && response.get(0)
                .getQueryContinue()
                .getAdditionalProperties() != null && !response.get(0)
                .getQueryContinue()
                .getAdditionalProperties()
                .isEmpty()) {
            HashMap queryContinueValue = (HashMap) response.get(0)
                    .getQueryContinue()
                    .getAdditionalProperties()
                    .entrySet()
                    .iterator()
                    .next()
                    .getValue();
            return ((Map.Entry<String, String>) queryContinueValue.entrySet()
                    .iterator()
                    .next()).getValue();
        } else {
            return null;
        }
    }

    private BaseItem build_valid_BaseItem(Map<String, Object> additionalProperties,
            String id_fieldName,
            String name_fieldName) {
        BaseItem baseItem = new BaseItem();

        try {
            if (additionalProperties.get(id_fieldName) != null) {
                baseItem.setId(additionalProperties.get(id_fieldName)
                        .toString());
            }
            if (additionalProperties.get(name_fieldName) != null) {
                baseItem.setName((String) additionalProperties.get(name_fieldName));
            }
        } catch (Exception ex) {
            logger.error("Error building valid BaseItem.", ex);
        }

        return baseItem.isValidBaseItem() ? baseItem : null;
    }

    protected void setImageData(BaseItem baseItem) {
        try {
            String imageUrl;

            if (baseItem instanceof TableEntry) {
                imageUrl = getImageUrl_forFile(((TableEntry) baseItem).getImage());
            } else {
                imageUrl = getImageUrl_forPage(baseItem.getId());
            }

            if (!isNullOrEmpty(imageUrl)) {
                String imageFileName = downloadImage(imageUrl);

                if (!isNullOrEmpty(imageFileName)) {
                    baseItem.setImageFileName(imageFileName);
                    baseItem.setImageUrl(imageUrl);
                }
            }
        } catch (Exception ex) {
            logger.error("Error setting image data for: " + baseItem.getName(), ex);
        }
    }

    private final String THUMBNAIL = "/revision/latest/thumbnail/width/288/height/288";

    private String downloadImage(String imageUrl) {
        String imageFileName;
        String encodedFileName = null;

        if (checkDownloadPath()) {
            imageFileName = GetImageFilename_fromUrl(imageUrl);

            if (!isNullOrEmpty(imageFileName)) {
                encodedFileName = URIDecode(imageFileName);

                if (!Paths.get(downloadPath, encodedFileName)
                        .toFile()
                        .exists()) {
                    String thumbnailUrl =
                            imageUrl.substring(0, imageUrl.indexOf(imageFileName) + imageFileName.length()) + THUMBNAIL;
                    if (!downloadFile(downloadPath, thumbnailUrl, encodedFileName)) {
                        encodedFileName = null;
                    }
                }
            }
        }
        return encodedFileName;
    }

    private boolean checkDownloadPath() {
        if (!isNullOrEmpty(this.downloadPath)) {
            final File dataFolder = new File(this.downloadPath);
            if (!dataFolder.exists()) {
                if (!dataFolder.mkdirs()) {
                    throw new ImportComponentException("Couldn't create download folder data directory.");
                } else {
                    logger.debug("creating download path");
                }
            }

            return true;
        } else {
            return false;
        }
    }

}
