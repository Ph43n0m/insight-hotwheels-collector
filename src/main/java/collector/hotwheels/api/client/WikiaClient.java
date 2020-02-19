package collector.hotwheels.api.client;

import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.api.model.Category;
import collector.hotwheels.api.model.PageData;
import collector.hotwheels.api.model.RevisionContent;
import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.util.CacheProvider;
import collector.hotwheels.insight.util.rest.EndPointSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.SizeLimitExceededException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static collector.hotwheels.api.client.WikiaContentParser.GetCardValues;
import static collector.hotwheels.api.client.WikiaContentParser.GetCastingValues;
import static collector.hotwheels.api.client.WikiaContentParser.GetDescription;
import static collector.hotwheels.api.client.WikiaContentParser.GetSpecificationValues;
import static collector.hotwheels.api.client.WikiaContentParser.GetTableEntries;
import static collector.hotwheels.insight.util.CollectionUtils.pickValues;
import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public final class WikiaClient {

    private final Logger logger = LoggerFactory.getLogger(WikiaClient.class);

    private EndPointSettings endPointSettings;
    private int requestLimit = -1;
    private String dataFolder;
    private boolean downloadImages;

    private WikiaClient(WikiaClient.Builder builder) {
        this.endPointSettings = builder.endPointSettings;
        this.requestLimit = builder.requestLimit;
        this.dataFolder = builder.dataFolder;
        this.downloadImages = builder.downloadImages;
    }

    public Categories categories() {
        return new Categories();
    }

    public Models models() {
        return new Models();
    }

    public Collections collections() {
        return new Collections();
    }

    public Toys toys() {
        return new Toys();
    }

    public Pages pages() {
        return new Pages();
    }

    public boolean isValidClient() {
        return (new ApiInfo()).isValid();
    }

    public static class Builder {

        private String dataFolder;
        private EndPointSettings endPointSettings;
        private int requestLimit = 0;
        private boolean downloadImages;

        public Builder(String dataFolder, EndPointSettings endPointSettings) {
            this.endPointSettings = endPointSettings;
            this.dataFolder = dataFolder;
        }

        public WikiaClient build() {
            return new WikiaClient(this);
        }

        public WikiaClient.Builder setRequestLimit(int value) {
            this.requestLimit = value;
            return this;
        }

        public WikiaClient.Builder setDownloadImages(boolean value) {
            this.downloadImages = value;
            return this;
        }
    }

    public class ApiInfo extends WikiaRestClient {

        private ApiInfo() {
            super(dataFolder, endPointSettings, 1);
        }

        public boolean isValid() {
            return IsValid();
        }
    }

    public class Categories extends WikiaRestClient {

        private Categories() {
            super(dataFolder, endPointSettings, requestLimit);
        }

        public WikiaClient.Categories.ListData list() {
            WikiaClient.Categories.ListData resultList = new WikiaClient.Categories.ListData();
            return resultList;
        }

        public class ListData {

            protected ListData() {

            }

            public Set<Category> execute() {
                Set<Category> resultCategories = new HashSet<>();

                Set<PageData> pageData = pages().list()
                        .execute();

                if (pageData != null && !pageData.isEmpty()) {
                    for (PageData page : pageData) {
                        if (page != null && page.getCategories() != null && !page.getCategories()
                                .isEmpty()) {
                            resultCategories.addAll(page.getCategories());
                        }
                    }
                }
                if (downloadImages) {
                    for (BaseItem baseItem : resultCategories) {
                        setImageData(baseItem);
                    }
                }

                return resultCategories;
            }
        }
    }

    public class Models extends WikiaRestClient {

        private Models() {
            super(dataFolder, endPointSettings, requestLimit);
        }

        public WikiaClient.Models.ListData list() {
            WikiaClient.Models.ListData resultList = new WikiaClient.Models.ListData();
            return resultList;
        }

        public class ListData {

            protected ListData() {

            }

            public Set<PageData> execute() {
                Set<PageData> resultModels = new HashSet<>();

                Set<PageData> pageData = pages().list()
                        .execute();

                if (pageData != null && !pageData.isEmpty()) {
                    resultModels = pageData.stream()
                            .filter(PageData::getHasCastingData)
                            .collect(Collectors.toSet());
                }
                if (downloadImages) {
                    for (BaseItem baseItem : resultModels) {
                        setImageData(baseItem);
                    }
                }

                return resultModels;
            }
        }
    }

    public class Collections extends WikiaRestClient {

        private Collections() {
            super(dataFolder, endPointSettings, requestLimit);
        }

        public WikiaClient.Collections.ListData list() {
            WikiaClient.Collections.ListData resultList = new WikiaClient.Collections.ListData();
            return resultList;
        }

        public class ListData {

            protected ListData() {

            }

            public Set<PageData> execute() {
                Set<PageData> resultCollections = new HashSet<>();

                Set<PageData> pageData = pages().list()
                        .execute();

                if (pageData != null && !pageData.isEmpty()) {
                    resultCollections = pageData.stream()
                            .filter(o -> !o.getHasCastingData())
                            .collect(Collectors.toSet());
                }
                if (downloadImages) {
                    for (BaseItem baseItem : resultCollections) {
                        setImageData(baseItem);
                    }
                }

                return resultCollections;
            }
        }
    }

    public class Toys extends WikiaRestClient {

        private Toys() {
            super(dataFolder, endPointSettings, requestLimit);
        }

        public WikiaClient.Toys.ListData list() {
            WikiaClient.Toys.ListData resultList = new WikiaClient.Toys.ListData();
            return resultList;
        }

        public class ListData {

            protected ListData() {

            }

            public Set<TableEntry> execute() {
                Set<TableEntry> resultToys = new HashSet<>();

                Set<PageData> pageData = pages().list()
                        .execute();

                if (pageData != null && !pageData.isEmpty()) {
                    pageData = pageData.stream()
                            .filter(PageData::getHasCastingData)
                            .collect(Collectors.toSet());
                }

                if (pageData != null && !pageData.isEmpty()) {
                    for (PageData page : pageData) {
                        if (page != null && page.getTableEntries() != null && !page.getTableEntries()
                                .isEmpty()) {
                            resultToys.addAll(page.getTableEntries());
                        }
                    }
                }
                if (downloadImages) {
                    for (BaseItem baseItem : resultToys) {
                        setImageData(baseItem);
                    }
                }

                return resultToys;
            }
        }
    }

    public class Pages extends WikiaRestClient {

        private String pageId;

        private Pages() {
            super(dataFolder, endPointSettings, requestLimit);
        }

        public WikiaClient.Pages.ListData list() {
            WikiaClient.Pages.ListData resultList = new WikiaClient.Pages.ListData(this.pageId);
            return resultList;
        }

        public Pages setPageId(String pageId) {
            this.pageId = pageId;
            return this;
        }

        public class ListData implements Supplier<Set<PageData>> {

            private static final int MAX_PAGE_ID_AMOUNT = 50;
            private String pageId;

            protected ListData(String pageId) {
                this.pageId = pageId;
            }

            public Set<PageData> execute() {
                Set<PageData> ret = new HashSet<>();
                try {
                    Object result = CacheProvider.getInstance()
                            .get(this.getClass()
                                    .getCanonicalName() + requestLimit +
                                    (isNullOrEmpty(this.pageId) ? "" : this.pageId), this);
                    if (result != null) {
                        ret = (Set<PageData>) result;
                    }
                } catch (Exception ee) {
                    logger.error("Error fetching Model data from api endpoint.", ee);
                }

                return ret;
            }

            @Override
            public Set<PageData> get() {
                Set<PageData> resultPageData = new HashSet<>();

                if (!isNullOrEmpty(this.pageId)) {
                    BaseItem baseItem = new BaseItem();
                    baseItem.setId(this.pageId);
                    PageData singlepage = new PageData(baseItem);
                    resultPageData.add(singlepage);
                } else {
                    resultPageData = getAllPageData();
                }

                enrichPageData(resultPageData);

                resultPageData = resultPageData.stream()
                        .filter(o -> !o.getRevisionContent()
                                .getContent()
                                .contains("#REDIRECT"))
                        .collect(Collectors.toSet());

                return resultPageData;
            }

            private Set<PageData> getAllPageData() {
                Set<PageData> resultPageData = new HashSet<>();

                try {

                    Set<Category> categories = fetchCategories();

                    if (!categories.isEmpty()) {
                        for (Category category : categories) {
                            if (category != null && category.getCategoryMembers() != null &&
                                    !category.getCategoryMembers()
                                            .isEmpty()) {
                                for (BaseItem categoryMember : category.getCategoryMembers()) {
                                    if (categoryMember != null) {
                                        PageData articlePageData = new PageData(categoryMember);
                                        PageData existingPageData = resultPageData.stream()
                                                .filter(o -> o.equals(articlePageData))
                                                .findFirst()
                                                .orElse(null);

                                        if (existingPageData == null) {
                                            articlePageData.getCategories()
                                                    .add(category);
                                            resultPageData.add(articlePageData);
                                        } else {
                                            existingPageData.getCategories()
                                                    .add(category);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Error getting full content page data.", ex);
                }
                return resultPageData;
            }

            /*
                2815    :   Designers
                2169    :   Series
                1792    :   Wheel Types
                4653    :   Wheel Types
                22154   :   Wheel Types (By Type)
                16498   :   Hot Wheels Car Errors
                9489    :   Kar Keepers
                19838   :   Blister Card Variations
                2172    :   Manufacturer

                13353   :   Corvette
                51290   :   ERF Vehicles
                2695    :   Ferrari
                13175   :   Lamborghini
                22414   :   Peugeot Citroën Cars
                24785   :   Renault Cars
                52379   :   Triumph Motor Company Cars

                2003    :   Components
                3862    :   Chassis
                8831    :   Window Color
                2969    :   ZAMAC
                3032    :   Definitions
                13511   :   'Tooned
                68694   :   Base code
                68459   :   Redirects
                4272    :   Bent-axle suspension
                9772    :   Blings
                2425    :   Card
                36033   :   Cast Number
                7614    :   Chase
                3862    :   Chassis
                5460    :   Code 3
                19075   :   Date Codes(s)
                18671   :   Date Codes
                3999    :   Die-cast
                6166    :   DiGS
                20264   :   FEP
                9928    :   Final Run
                5420    :   HWC.com
                3861    :   Mainline
                18755   :   Mattel Exclusive
                22644   :   Articles
                7486    :   Tips On How To Photograph Hot Wheels
                15611   :   Paint Finishes
                36319   :   Retool
                13288   :   Segment Series
                3180    :   Spectraflame
                39288   :   Spectrafrost
                7330    :   Tampo
                4275    :   Tool
                68465   :   Toy Number
                1781    :   Treasure Hunt
                1795    :   Treasure Hunts
                5114    :   TRU
                2007    :   History
                37317   :   Bob Bio at Mattel Toys
                1783    :   History of Hot Wheels
                36425   :   History of the Hot Wheels Blisterpacks
                1782    :   Mattel
                2257    :   Disambiguation
                67750   :   (Deleted)
                86061   :   ,..
                56668   :   Candidates for deletion
             */
            private final String[] UNWANTED_CATEGORIES =
                    {"2815", "2169", "1792", "4653", "22154", "16498", "9489", "19838", "2172",

                            "13353", "51290", "2695", "13175", "22414", "24785", "52379",

                            "2003", "3862", "8831", "2969", "3032", "13511", "68694", "68459", "4272", "9772", "2425",
                            "36033", "7614", "3862", "5460", "19075", "18671", "3999", "6166", "20264", "9928", "5420",
                            "3861", "18755", "22644", "7486", "15611", "36319", "13288", "3180", "39288", "7330",
                            "4275", "68465", "1781", "1795", "5114", "2007", "37317", "1783", "36425", "1782", "2172",
                            "2257", "67750", "86061", "56668"};

            private Set<Category> fetchCategories() {
                Set<Category> resultCategories = new HashSet<>();

                Set<BaseItem> allCategoryList = GetAllCategoryList();

                if (allCategoryList != null && !allCategoryList.isEmpty()) {
                    for (BaseItem categoryItem : allCategoryList) {
                        if (!Arrays.stream(UNWANTED_CATEGORIES)
                                .anyMatch(categoryItem.getId()::equals)) {

                            Category category = new Category(categoryItem);

                            Set<BaseItem> categoryMembers = GetCategoryMember(category.getId());

                            for (BaseItem categoryMember : categoryMembers) {
                                if (!Arrays.stream(UNWANTED_CATEGORIES)
                                        .anyMatch(categoryMember.getId()::equals)) {
                                    category.getCategoryMembers()
                                            .add(categoryMember);
                                }

                                resultCategories.add(category);
                            }
                        }
                    }
                }

                return resultCategories;
            }

            private void enrichPageData(Set<PageData> pageData) {
                try {
                    if (pageData != null && !pageData.isEmpty()) {
                        Set<RevisionContent> revisionContentSet = getRevisionContent(pageData);

                        if (!revisionContentSet.isEmpty()) {
                            for (PageData page : pageData) {
                                page.setRevisionContent(revisionContentSet.stream()
                                        .filter(o -> page.getId()
                                                .equals(o.getPageid()))
                                        .findFirst()
                                        .orElse(null));

                                SetPageAttributes(page);
                                SetDescription_forPage(page);
                                SetTableEntries_forPage(page);
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Error set revision content for models.");
                }
            }

            private Set<RevisionContent> getRevisionContent(Set<PageData> pageData) {
                Set<RevisionContent> revisionContentSet = new HashSet<>();

                List<String> pageIds = pickValues(pageData, "getId");

                if (!pageIds.isEmpty()) {

                    while (!pageIds.isEmpty()) {
                        try {
                            revisionContentSet.addAll(GetRevisionContentList(getPageIdParameterList(
                                    pageIds.subList(0, Math.min(pageIds.size(), MAX_PAGE_ID_AMOUNT)))));
                        } catch (Exception ce) {
                            logger.error("Error getting revision content.");
                        }
                        pageIds.subList(0, Math.min(pageIds.size(), MAX_PAGE_ID_AMOUNT))
                                .clear();
                    }
                }
                return revisionContentSet;
            }

            private String getPageIdParameterList(List<String> pageids) throws Exception {
                String ret;

                if (pageids == null) {
                    throw new NullPointerException();
                } else if (pageids.isEmpty()) {
                    throw new Exception("PageId list is empty!");
                } else if (pageids.size() > MAX_PAGE_ID_AMOUNT) {
                    throw new SizeLimitExceededException();
                }

                ret = String.join("|", pageids);

                return ret;
            }

            private void SetPageAttributes(PageData pageData) {
                try {
                    if (pageData != null && pageData.getRevisionContent() != null && !isNullOrEmpty(
                            pageData.getRevisionContent()
                                    .getContent())) {

                        Map<String, String> castingData = GetCastingValues(pageData.getRevisionContent());
                        pageData.setHasCastingData(!castingData.isEmpty());
                        castingData.putAll(GetSpecificationValues(pageData.getRevisionContent()));
                        castingData.putAll(GetCardValues(pageData.getRevisionContent()));

                        if (!castingData.isEmpty()) {
                            castingData.forEach((k, v) -> {
                                if (!isNullOrEmpty(v)) {

                                    if (k.toLowerCase()
                                            .contains("acceleration") || k.toLowerCase()
                                            .contains("0-")) {
                                        pageData.setAcceleration(v);
                                    } else if (k.toLowerCase()
                                            .contains("designer") && !k.toLowerCase()
                                            .equals("cardesigner")) {
                                        pageData.setDesigner(v);
                                    } else if (k.toLowerCase()
                                            .contains("speed")) {
                                        pageData.setTopSpeed(v);
                                    } else if (k.toLowerCase()
                                            .contains("power")) {
                                        pageData.setHorsepower(v);
                                    } else {
                                        switch (k.toLowerCase()
                                                .trim()) {
                                            case "series":
                                                pageData.setDebutSeries(v);
                                                break;
                                            case "image":
                                                if (isNullOrEmpty(pageData.getImage())) {
                                                    pageData.setImage(v);
                                                } else {
                                                    String asdasd = "";
                                                }
                                                break;
                                            case "years":
                                                pageData.setProducingYears(v);
                                                break;
                                            case "production":
                                                pageData.setProduction(v);
                                                break;
                                            case "number":
                                                pageData.setToyNumber(v);
                                                break;
                                            case "engine":
                                                pageData.setEngine(v);
                                                break;
                                            case "name":
                                                if (isNullOrEmpty(pageData.getName())) {
                                                    pageData.setName(v);
                                                }
                                                break;
                                            case "born":
                                                pageData.setBorn(v);
                                                break;
                                            case "birthplace":
                                                pageData.setBirthplace(v);
                                                break;
                                            case "specialty":
                                                pageData.setSpecialty(v);
                                                break;
                                            case "cardesigner":
                                                pageData.setCarDesigner(v);
                                                break;
                                            default:
                                                logger.debug("Missing case for: " + k);
                                                break;
                                        }
                                    }

                                }
                            });
                        } else {
                            pageData.setHasCastingData(false);
                        }

                        if (isNullOrEmpty(pageData.getName())) {
                            pageData.setName(pageData.getRevisionContent()
                                    .getTitle());
                        }

                    }

                } catch (Exception ex) {
                    logger.error("Error set casting data for model: " + pageData.getName());
                }
            }

            private void SetDescription_forPage(PageData pageData) {
                try {
                    if (pageData != null && pageData.getRevisionContent() != null && !isNullOrEmpty(
                            pageData.getRevisionContent()
                                    .getContent())) {
                        pageData.setDescription(GetDescription(pageData.getRevisionContent()));
                    }

                } catch (Exception ex) {
                    logger.error("Error set description for page: " + pageData.getName());
                }
            }

            private void SetTableEntries_forPage(PageData pageData) {
                try {
                    if (pageData != null && pageData.getRevisionContent() != null && !isNullOrEmpty(
                            pageData.getRevisionContent()
                                    .getContent())) {

                        Set<TableEntry> tableEntries = GetTableEntries(pageData, pageData.getRevisionContent());

                        if (!tableEntries.isEmpty()) {
                            pageData.setTableEntries(tableEntries);
                        }
                    }

                } catch (Exception ex) {
                    logger.error("Error set versions for page: " + pageData.getName());
                }
            }

        }

    }

}
