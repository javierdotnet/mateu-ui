package mateu.ui.core.components;

/**
 * Created by miguel on 10/8/16.
 */
public class BaseComponent implements Component {

    private int colsXs = 12;

    private int colsSm = 6;

    private int colsMd = 3;

    private int colsLg = 3;

    private int offsetXs = 0;

    private int offsetSm = 0;

    private int offsetMd = 0;

    private int offsetLg = 0;

    private boolean visibleXs = true;

    private boolean visibleSm = true;

    private boolean visibleMd = true;

    private boolean visibleLg = true;




    public int getColsXs() {
        return colsXs;
    }

    public void setColsXs(int colsXs) {
        this.colsXs = colsXs;
    }

    public int getColsSm() {
        return colsSm;
    }

    public void setColsSm(int colsSm) {
        this.colsSm = colsSm;
    }

    public int getColsMd() {
        return colsMd;
    }

    public void setColsMd(int colsMd) {
        this.colsMd = colsMd;
    }

    public int getColsLg() {
        return colsLg;
    }

    public void setColsLg(int colsLg) {
        this.colsLg = colsLg;
    }

    public int getOffsetXs() {
        return offsetXs;
    }

    public void setOffsetXs(int offsetXs) {
        this.offsetXs = offsetXs;
    }

    public int getOffsetSm() {
        return offsetSm;
    }

    public void setOffsetSm(int offsetSm) {
        this.offsetSm = offsetSm;
    }

    public int getOffsetMd() {
        return offsetMd;
    }

    public void setOffsetMd(int offsetMd) {
        this.offsetMd = offsetMd;
    }

    public int getOffsetLg() {
        return offsetLg;
    }

    public void setOffsetLg(int offsetLg) {
        this.offsetLg = offsetLg;
    }

    public boolean isVisibleXs() {
        return visibleXs;
    }

    public void setVisibleXs(boolean visibleXs) {
        this.visibleXs = visibleXs;
    }

    public boolean isVisibleSm() {
        return visibleSm;
    }

    public void setVisibleSm(boolean visibleSm) {
        this.visibleSm = visibleSm;
    }

    public boolean isVisibleMd() {
        return visibleMd;
    }

    public void setVisibleMd(boolean visibleMd) {
        this.visibleMd = visibleMd;
    }

    public boolean isVisibleLg() {
        return visibleLg;
    }

    public void setVisibleLg(boolean visibleLg) {
        this.visibleLg = visibleLg;
    }

}
