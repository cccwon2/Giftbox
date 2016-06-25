package me.memorytalk.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.memorytalk.common.json.DateSerializer;

import java.util.Date;
import java.util.List;

@Data
public class AdminEventDetailModel {

    private Long id;

    private String title;

    private String description;

    private String company;

    private String eventType;

    private String eventPage;

    private String homePage;

    @JsonSerialize(using = DateSerializer.class)
    private Date startDate;

    @JsonSerialize(using = DateSerializer.class)
    private Date endDate;

    @JsonSerialize(using = DateSerializer.class)
    private Date publicationDate;

    @JsonSerialize(using = DateSerializer.class)
    private Date registrationDate;

    @JsonSerialize(using = DateSerializer.class)
    private Date createdDate;

    @JsonSerialize(using = DateSerializer.class)
    private Date updatedDate;

    private boolean premium;

    private boolean visible;

    private List<AttachmentModel> attachments;

    private List<GiftModel> gifts;

    private List<String> tags;

    public AdminEventDetailModel(Long id, String title, String description, String company, String eventType, String eventPage, String homePage,
                                 Date startDate, Date endDate, Date publicationDate, Date registrationDate, Date createdDate, Date updatedDate,
                                 boolean premium, boolean visible) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.company = company;
        this.eventType = eventType;
        this.eventPage = eventPage;
        this.homePage = homePage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.publicationDate = publicationDate;
        this.registrationDate = registrationDate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.premium = premium;
        this.visible = visible;
    }

}
