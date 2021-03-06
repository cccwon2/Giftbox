package me.memorytalk.service;

import com.microsoft.azure.storage.StorageException;
import me.memorytalk.common.base.BaseObject;
import me.memorytalk.common.constant.GlobalConst;
import me.memorytalk.domain.*;
import me.memorytalk.dto.*;
import me.memorytalk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class EventService extends BaseObject {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeCodeRepository eventTypeCodeRepository;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private GiftTypeRepository giftTypeRepository;

    @Autowired
    private GiftTypeCodeRepository giftTypeCodeRepository;

    public Page<EventModel> getEvents(String premium, String sort, List<String> onGoings, List<Long> eventTypeCodes, int page) {

        Pageable pageable = new PageRequest(page - 1, GlobalConst.PAGE_SIZE);

        Pageable eventPageable;
        Page<EventModel> eventModels;

        if("true".equals(premium)) {
            eventModels = eventRepository.findPremiumEventModels(pageable);

            for(EventModel eventModel : eventModels) {
                List<EventTypeModel> eventTypes = eventTypeRepository.findEventTypeModels(eventModel.getId());
                eventModel.setEventTypes(eventTypes);

                List<String> giftTypes = giftTypeRepository.findGiftTypeModels(eventModel.getId());
                eventModel.setGiftTypes(giftTypes);
            }

            return eventModels;
        } else if("".equals(premium)) {
            //TimeZone tz = TimeZone.getDefault();
            //Calendar cal = GregorianCalendar.getInstance(tz);
            //int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
            //Date now = new Date(new Date().getTime() + offsetInMillis);
            long hour = 3600*1000;
            Date now = new Date(new Date().getTime() + 9*hour);
            logger.info("Now(UTC): " + now.toString());
            //System.err.println("Now(UTC): " + now.toString());

            List<EventModel> eventModelList = new ArrayList<>();
            long totalPremiumEvent = eventRepository.countPremiumEventModels(now, sort, onGoings, eventTypeCodes);

            if(totalPremiumEvent > 0) {
                eventPageable = new PageRequest(page - 1, GlobalConst.EVENT_PAGE_SIZE);
                eventModels = eventRepository.findEventModels(now, sort, onGoings, eventTypeCodes, eventPageable);

                List<EventModel> premiumEventModelList= eventRepository.findPremiumEventModels(now, sort, onGoings, eventTypeCodes);
                int eventModelsSize = eventModels.getContent().size();
                //System.err.println("eventModelsSize: " + eventModelsSize);
                long totalEvent = eventRepository.countEventModels(now, sort, onGoings, eventTypeCodes);
                //System.err.println("totalEvent: " + totalEvent);

                //System.err.println("totalPremiumEvent: " + totalPremiumEvent);
                long total = totalEvent + totalPremiumEvent;
                // premium total 구함
                // 일반 이벤트 9건당 프리미엄 이벤트 1건...
                EventModel premiumEventMode = null;
                //System.err.println("total: " + total);
                long quotient = (totalPremiumEvent / page);
                if(quotient > 0) {
                    premiumEventMode = premiumEventModelList.get(page - 1);
                } else {
                    // 페이지는 계속 늘지만 나머지는 고정...
                    // 페이지 - 나머지, 총 프리미엄 개수...
                    // 재귀 호출
                    int totalPremiumCount = (int)totalPremiumEvent;
                    int premiumEventIndex = recursiveCallMinus(totalPremiumCount - page, totalPremiumCount);
                    premiumEventMode = premiumEventModelList.get(premiumEventIndex);
                }
                if(eventModelsSize < 4) {
                    for(EventModel eventModel: eventModels) {
                        eventModelList.add(eventModel);
                    }
                    if(premiumEventMode != null) {
                        eventModelList.add(premiumEventMode);
                    }
                } else {
                    eventModelList.add(eventModels.getContent().get(0));
                    eventModelList.add(eventModels.getContent().get(1));
                    eventModelList.add(eventModels.getContent().get(2));
                    eventModelList.add(eventModels.getContent().get(3));
                    if(premiumEventMode != null) {
                        eventModelList.add(premiumEventMode);
                    }
                    for(int i=4; i<eventModelsSize; i++) {
                        eventModelList.add(eventModels.getContent().get(i));
                    }
                }
                for (EventModel eventModel : eventModelList) {
                    List<EventTypeModel> eventTypes = eventTypeRepository.findEventTypeModels(eventModel.getId());
                    eventModel.setEventTypes(eventTypes);

                    List<String> giftTypes = giftTypeRepository.findGiftTypeModels(eventModel.getId());
                    eventModel.setGiftTypes(giftTypes);
                }

                return new PageImpl<>(eventModelList, pageable, total);
            } else {
                eventPageable = new PageRequest(page - 1, GlobalConst.PAGE_SIZE);
                eventModels = eventRepository.findEventModels(now, sort, onGoings, eventTypeCodes, eventPageable);

                for (EventModel eventModel : eventModels) {
                    List<EventTypeModel> eventTypes = eventTypeRepository.findEventTypeModels(eventModel.getId());
                    eventModel.setEventTypes(eventTypes);

                    List<String> giftTypes = giftTypeRepository.findGiftTypeModels(eventModel.getId());
                    eventModel.setGiftTypes(giftTypes);
                }

                return eventModels;
            }
        } else {
            return null;
        }
    }

    public EventDetailModel getEvent(Long eventId) {

        EventDetailModel eventDetailModel = eventRepository.findEventModel(eventId);
        eventDetailModel.setAttachments(attachmentRepository.findAttachmentModels(eventId));
        eventDetailModel.setGifts(giftRepository.findGiftModels(eventId));
        eventDetailModel.setEventTypes(eventTypeRepository.findEventTypeModels(eventId));

        return eventDetailModel;
    }

    public Page<AdminEventModel> getAdminEvents(String eventId, String eventTitle, String premium, String visible, Pageable pageable) {
        return eventRepository.findAdminEventModels(eventId, eventTitle, premium, visible, pageable);
    }

    public Boolean addAdminEvent(AdminEventDetailForm requestForm, MultipartFile file)
            throws IOException, InvalidKeyException, StorageException, URISyntaxException, ParseException {

        uploadService.validateImageFile(file);

        Date startDate = null;
        Date endDate = null;
        Date publicationDate = null;

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        if(requestForm.getStartDate() != null) {
            SimpleDateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            String startDateToString = startDateFormat.format(requestForm.getStartDate());
            startDate = transFormat.parse(startDateToString);
            startDate = new Date(startDate.getTime() + offsetInMillis);
        }

        if(requestForm.getEndDate() != null) {
            SimpleDateFormat endDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            String endDateToString = endDateFormat.format(requestForm.getEndDate());
            endDate = transFormat.parse(endDateToString);
            endDate = new Date(endDate.getTime() + offsetInMillis);
        }

        if(requestForm.getPublicationDate() != null) {
            SimpleDateFormat publicationDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            String publicationDateToString = publicationDateFormat.format(requestForm.getPublicationDate());
            publicationDate = transFormat.parse(publicationDateToString);
            publicationDate = new Date(publicationDate.getTime() + offsetInMillis);
        }

        Event event = new Event();
        event.setTitle(requestForm.getTitle());
        event.setDescription(requestForm.getDescription());
        event.setCompany(requestForm.getCompany());
        event.setEventTarget(requestForm.getEventTarget());
        event.setEventPage(requestForm.getEventPage());
        event.setPrizePage(requestForm.getPrizePage());
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setPublicationDate(publicationDate);
        event.setPublicationContent1(requestForm.getPublicationContent1());
        event.setPublicationContent2(requestForm.getPublicationContent2());
        event.setPublicationType(requestForm.getPublicationType());
        event.setPremium(requestForm.isPremium());
        event.setVisible(requestForm.isVisible());
        event.setCreatedDate(new Date());
        eventRepository.save(event);

        uploadService.uploadEventAttachment(event, file);

        addGifts(requestForm.getGifts(), event);
        addEventTypes(requestForm.getEventTypeCodeIds(), event);
        addGiftTypes(requestForm.getGiftTypeCodeIds(), event);

        return Boolean.TRUE;
    }

    public AdminEventDetailModel getAdminEvent(Long eventId) {

        AdminEventDetailModel adminEventDetailModel = eventRepository.findAdminEventModel(eventId);
        adminEventDetailModel.setAttachments(attachmentRepository.findAttachmentModels(eventId));
        adminEventDetailModel.setGifts(giftRepository.findGiftModels(eventId));
        adminEventDetailModel.setEventTypeCodeIds(eventTypeRepository.findEventTypeCodeIds(eventId));
        adminEventDetailModel.setGiftTypeCodeIds(giftTypeRepository.findGiftTypeCodeIds(eventId));

        return adminEventDetailModel;
    }

    public Boolean editAdminEvent(Long eventId, AdminEventDetailForm requestForm) throws ParseException {

        Date startDate = null;
        Date endDate = null;
        Date publicationDate = null;

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        if(requestForm.getStartDate() != null) {
            SimpleDateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            String startDateToString = startDateFormat.format(requestForm.getStartDate());
            startDate = transFormat.parse(startDateToString);
            startDate = new Date(startDate.getTime() + offsetInMillis);
        }

        if(requestForm.getEndDate() != null) {
            SimpleDateFormat endDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            String endDateToString = endDateFormat.format(requestForm.getEndDate());
            endDate = transFormat.parse(endDateToString);
            endDate = new Date(endDate.getTime() + offsetInMillis);
        }

        if(requestForm.getPublicationDate() != null) {
            SimpleDateFormat publicationDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            String publicationDateToString = publicationDateFormat.format(requestForm.getPublicationDate());
            publicationDate = transFormat.parse(publicationDateToString);
            publicationDate = new Date(publicationDate.getTime() + offsetInMillis);
        }

        Event event = eventRepository.findById(eventId);
        event.setTitle(requestForm.getTitle());
        event.setDescription(requestForm.getDescription());
        event.setCompany(requestForm.getCompany());
        event.setEventTarget(requestForm.getEventTarget());
        event.setEventPage(requestForm.getEventPage());
        event.setPrizePage(requestForm.getPrizePage());
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setPublicationDate(publicationDate);
        event.setPublicationContent1(requestForm.getPublicationContent1());
        event.setPublicationContent2(requestForm.getPublicationContent2());
        event.setPublicationType(requestForm.getPublicationType());
        event.setPremium(requestForm.isPremium());
        event.setVisible(requestForm.isVisible());
        eventRepository.save(event);

        editGifts(requestForm.getGifts(), event);
        editEventTypes(requestForm.getEventTypeCodeIds(), event);
        editGiftTypes(requestForm.getGiftTypeCodeIds(), event);

        return Boolean.TRUE;
    }

    public Boolean setAdminEventVisible(Long eventId, boolean visible) {

        Event event = eventRepository.findById(eventId);
        event.setVisible(visible);
        eventRepository.save(event);

        return Boolean.TRUE;
    }

    public Boolean setAdminEventPremium(Long eventId, boolean premium) {

        Event event = eventRepository.findById(eventId);
        event.setPremium(premium);
        eventRepository.save(event);

        return Boolean.TRUE;
    }

    public Boolean removeAdminEvent(Long eventId) {

        eventRepository.delete(eventId);

        return Boolean.TRUE;
    }

    private void addGifts(List<AdminGiftForm> adminGiftForms, Event event) {

        if(adminGiftForms != null && adminGiftForms.size() > 0) {
            for(AdminGiftForm adminGiftForm : adminGiftForms) {
                Gift gift = new Gift();
                gift.setProduct(adminGiftForm.getProduct());
                gift.setCount(adminGiftForm.getCount());
                gift.setEvent(event);
                gift.setCreatedDate(new Date());
                giftRepository.save(gift);
            }
        }
    }

    private void editGifts(List<AdminGiftForm> adminGiftForms, Event event) {

        giftRepository.deleteByEventId(event.getId());
        if(adminGiftForms != null && adminGiftForms.size() > 0) {
            for(AdminGiftForm adminGiftForm : adminGiftForms) {
                Gift gift = new Gift();
                gift.setProduct(adminGiftForm.getProduct());
                gift.setCount(adminGiftForm.getCount());
                gift.setEvent(event);
                gift.setCreatedDate(new Date());
                giftRepository.save(gift);
            }
        }
    }

    private void addEventTypes(List<Long> eventTypeCodeIds, Event event) {

        if(eventTypeCodeIds.size() > 0) {
            for(Long eventTypeCodeId : eventTypeCodeIds) {
                EventTypeCode eventTypeCode = eventTypeCodeRepository.findById(eventTypeCodeId);
                EventType eventType = new EventType();
                eventType.setEvent(event);
                eventType.setEventTypeCode(eventTypeCode);
                eventType.setCreatedDate(new Date());
                eventTypeRepository.save(eventType);
            }
        }
    }

    private void editEventTypes(List<Long> eventTypeCodeIds, Event event) {

        eventTypeRepository.deleteByEventId(event.getId());
        if(eventTypeCodeIds.size() > 0) {
            for (Long eventTypeCodeId : eventTypeCodeIds) {
                EventTypeCode eventTypeCode = eventTypeCodeRepository.findById(eventTypeCodeId);
                EventType eventType = new EventType();
                eventType.setEvent(event);
                eventType.setEventTypeCode(eventTypeCode);
                eventType.setCreatedDate(new Date());
                eventTypeRepository.save(eventType);
            }
        }
    }

    private void addGiftTypes(List<Long> giftTypeCodeIds, Event event) {

        if(giftTypeCodeIds.size() > 0) {
            for(Long giftTypeCodeId : giftTypeCodeIds) {
                GiftTypeCode giftTypeCode = giftTypeCodeRepository.findById(giftTypeCodeId);
                GiftType giftType = new GiftType();
                giftType.setEvent(event);
                giftType.setGiftTypeCode(giftTypeCode);
                giftType.setCreatedDate(new Date());
                giftTypeRepository.save(giftType);
            }
        }
    }

    private void editGiftTypes(List<Long> giftTypeCodeIds, Event event) {

        giftTypeRepository.deleteByEventId(event.getId());
        if(giftTypeCodeIds.size() > 0) {
            for (Long giftTypeCodeId : giftTypeCodeIds) {
                GiftTypeCode giftTypeCode = giftTypeCodeRepository.findById(giftTypeCodeId);
                GiftType giftType = new GiftType();
                giftType.setEvent(event);
                giftType.setGiftTypeCode(giftTypeCode);
                giftType.setCreatedDate(new Date());
                giftTypeRepository.save(giftType);
            }
        }
    }

    private int recursiveCallMinus(int minus, int totalPremiumCount) {
        // page 3, minus -1, totalPremiumCount 2 일 경우 result 1, index 0
        // page 4, minus -2, totalPremiumCount 2 일 경우 result 0, index 1
        // minus + totalPremiumCount 가 0 이상일때까지 돌려준다
        int result = minus + totalPremiumCount;
        int index = -minus - 1;
        if(result >= 0) {
            return index;
        }
        // page 5, minus -3, totalPremiumCount 2 일 경우 result -1, index 2 재귀호출 대상
        // index >= totalPremiumCount 경우 index 에러
        return recursiveCallMinus(minus + totalPremiumCount, totalPremiumCount);
    }
}
