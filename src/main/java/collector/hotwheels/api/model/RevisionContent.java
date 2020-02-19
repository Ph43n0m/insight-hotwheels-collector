package collector.hotwheels.api.model;

import com.google.common.base.Objects;

public final class RevisionContent {

    private String pageid;
    private String title;
    private String content;

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RevisionContent that = (RevisionContent) o;
        return Objects.equal(pageid, that.pageid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pageid);
    }
}
