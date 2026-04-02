package by.zeus.demo.base.web.dto;
import java.io.Serializable;

public class BaseDTO implements Serializable {

    private Long id;

    private int version;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }
}
