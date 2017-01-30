package io.mateu.ui.core.shared;

/**
 * Created by miguel on 4/1/17.
 */
public class FileLocator extends Data {

    public FileLocator(long id, String fileName, String url) {
        setId(id);
        setFileName(fileName);
        setUrl(url);
    }

    public FileLocator(FileLocator value) {
        clear();
        if (value != null) {
            setId(value.getId());
            setFileName(value.getFileName());
            setUrl(value.getUrl());
        }
    }

    public String getUrl() {
        return get("_url");
    }

    public long getId() {
        return get("_id");
    }

    public void setId(long id) {
        set("_id", id);
    }

    public void setUrl(String url) {
        set("_url", url);
    }

    public String getFileName() {
        return get("_filename");
    }

    public void setFileName(String fileName) {
        set("_filename", fileName);
    }
}
