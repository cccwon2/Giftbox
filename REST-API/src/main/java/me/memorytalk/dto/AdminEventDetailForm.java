package me.memorytalk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AdminEventDetailForm {

    private String title;

    private String description;

    private String company;

    private String eventTarget;

    private String eventPage;

    private String prizePage;

    private Date startDate;

    private Date endDate;

    private Date publicationDate;

    private String publicationContent1;

    private String publicationContent2;

    private boolean premium;

    private boolean visible;

    private List<AdminGiftForm> gifts;

    private List<AdminEventTypeForm> eventTypes;

    @JsonCreator
    public AdminEventDetailForm(@JsonProperty("title") String title,
                                @JsonProperty("description") String description,
                                @JsonProperty("company") String company,
                                @JsonProperty("eventTarget") String eventTarget,
                                @JsonProperty("eventPage") String eventPage,
                                @JsonProperty("prizePage") String prizePage,
                                @JsonProperty("startDate") Date startDate,
                                @JsonProperty("endDate") Date endDate,
                                @JsonProperty("publicationDate") Date publicationDate,
                                @JsonProperty("publicationContent1") String publicationContent1,
                                @JsonProperty("publicationContent2") String publicationContent2,
                                @JsonProperty("premium") boolean premium,
                                @JsonProperty("visible") boolean visible,
                                @JsonProperty("gifts") List<AdminGiftForm> gifts,
                                @JsonProperty("eventTypes") List<AdminEventTypeForm> eventTypes) {
        this.title = title;
        this.description = description;
        this.company = company;
        this.eventTarget = eventTarget;
        this.eventPage = eventPage;
        this.prizePage = prizePage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.publicationDate = publicationDate;
        this.publicationContent1 = publicationContent1;
        this.publicationContent2 = publicationContent2;
        this.premium = premium;
        this.visible = visible;
        this.gifts = gifts;
        this.eventTypes = eventTypes;
    }
}
