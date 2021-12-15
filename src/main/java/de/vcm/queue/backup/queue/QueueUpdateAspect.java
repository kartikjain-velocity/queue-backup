//package de.vcm.queue.backup.queue;
//
//import de.rwth.idsg.bikeman.domain.Station;
//import de.rwth.idsg.bikeman.domain.StationSlot;
//import de.rwth.idsg.bikeman.psinterface.dto.request.BootNotificationDTO;
//import de.rwth.idsg.bikeman.queue.dto.SlotAzureQueueDTO;
//import de.rwth.idsg.bikeman.queue.dto.StationAzureQueueDTO;
//import de.rwth.idsg.bikeman.queue.dto.StationSlotAzureQueueDTO;
//import de.rwth.idsg.bikeman.queue.exception.QueueStorageException;
//import de.rwth.idsg.bikeman.queue.util.JsonUtil;
//import de.rwth.idsg.bikeman.service.StationService;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
//
//@Aspect
//@Component
//@Slf4j
//public class QueueUpdateAspect {
//    @Autowired
//    StationService stationService;
//
//    @Autowired
//    private QueueManagement queueManagement;
//
//    @Pointcut("execution(* de.rwth.idsg.bikeman.service.StationService.create(..))  || execution(* de.rwth.idsg.bikeman.service.StationService.updateStation(..))")
//    private void stationService() {}
//
//    @AfterReturning(
//            pointcut="stationService()",
//            returning="returnValue")
//    public void aroundExecution(JoinPoint pjp, Station returnValue) throws Throwable {
//        Station station = stationService.getStationByIdWithSlots(returnValue.getStationId());
//        StationAzureQueueDTO dto = convertToDto(station);
//      queueManagement.addMessage(JsonUtil.toJson(dto), QueueMessageChannel.STATION);
//      stationSlots(2);
//    }
//
//    private void stationSlots(long stationId) throws QueueStorageException {
//        Station station = stationService.getStationByIdWithSlots(stationId);
//        Set<StationSlot> slotList = station.getStationSlots();
//        Set<SlotAzureQueueDTO> slotSet = slotList.stream().map(SlotAzureQueueDTO::new).collect(Collectors.toSet());
//        StationSlotAzureQueueDTO stationSlotAzureQueueDTO = new StationSlotAzureQueueDTO();
//        stationSlotAzureQueueDTO.setStationId(station.getStationId());
//        stationSlotAzureQueueDTO.setStationSlots(slotSet);
//        queueManagement.addMessage(JsonUtil.toJson(stationSlotAzureQueueDTO), QueueMessageChannel.STATION_SLOT);
//
//    }
//
//    private StationAzureQueueDTO convertToDto(Station station) {
//       return  new StationAzureQueueDTO(station);
//    }
//
//
//
//    @After("execution(* de.rwth.idsg.bikeman.psinterface.rest.PsiService.handleBootNotification(..)) && args(bootNotificationDTO)" )
//    private void stationSlotsUpdate(JoinPoint pjp, BootNotificationDTO bootNotificationDTO) throws QueueStorageException {
//        Station station = stationService.getStationByManufactureIdWithSlots(bootNotificationDTO.getStationManufacturerId());
//        Set<StationSlot> slotList = station.getStationSlots();
//        Set<SlotAzureQueueDTO> slotSet = slotList.stream().map(SlotAzureQueueDTO::new).collect(Collectors.toSet());
//        StationSlotAzureQueueDTO stationSlotAzureQueueDTO = new StationSlotAzureQueueDTO();
//        stationSlotAzureQueueDTO.setStationId(station.getStationId());
//        stationSlotAzureQueueDTO.setStationSlots(slotSet);
//        queueManagement.addMessage(JsonUtil.toJson(stationSlotAzureQueueDTO), QueueMessageChannel.STATION_SLOT);
//    }
//
//}
