package me.memorytalk.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event implements Serializable {

    private static final long serialVersionUID = -1394199278362987746L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String company;

    @Column
    private String eventTarget;

    @Column
    private String eventPage;

    @Column
    private String prizePage;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column
    private Date publicationDate;

    @Column
    private String publicationContent1;

    @Column
    private String publicationContent2;

    @Column
    private String publicationType;

    @Column(nullable = false)
    private boolean premium;

    @Column(nullable = false)
    private boolean visible;

    @Column(nullable = false)
    private Date createdDate;

    @OneToMany(mappedBy="event", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<Attachment> attachments;

    @OneToMany(mappedBy="event", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<Gift> gifts;

    @OneToMany(mappedBy="event", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<EventType> eventTypes;

    @OneToMany(mappedBy="event", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<GiftType> giftTypes;

    @OneToMany(mappedBy = "event", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    private List<PushNotification> pushNotifications;
}
