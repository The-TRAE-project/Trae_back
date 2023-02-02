import java.util.Date;

public class Operation {
    Boolean hasDo;
    int performerUuid;
    Date startDate;
    Date stopDate;

    public Boolean getHasDo() {
        return hasDo;
    }

    public void setHasDo(Boolean hasDo) {
        this.hasDo = hasDo;
    }

    public int getPerformerUuid() {
        return performerUuid;
    }

    public void setPerformerUuid(int performerUuid) {
        this.performerUuid = performerUuid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }
}
