package com.example.tsb.services;

import com.example.tsb.models.Schedule;
import com.example.tsb.repositories.AppointmentRepository;
import com.example.tsb.repositories.ScheduleRepository;
import com.example.tsb.repositories.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;


    public ScheduleService(ScheduleRepository scheduleRepository, ServiceRepository serviceRepository, AppointmentService appointmentRepository, AppointmentRepository appointmentRepository1) {
        this.scheduleRepository = scheduleRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentService = appointmentRepository;
        this.appointmentRepository = appointmentRepository1;
    }
    public Map<String,List<LocalDateTime>> getDatesTimesForSchedules(){
        List<String> months=Arrays.asList("янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек");
        Map<String,List<LocalDateTime>> datesTimes=new LinkedHashMap<>();
        for( int i=0;i<30;i++){
            List <LocalDateTime> arr1=new ArrayList<>();
            LocalDateTime nowDate=LocalDateTime.now().plusDays(i+1);
            String key=LocalDateTime.now().plusDays(i+1).getDayOfMonth()+" "+months.get(nowDate.getMonthValue()-1);
            for (int j=10;j<=20;j++){
                LocalDateTime localDateTime=nowDate.withHour(j).
                        withMinute(0).withSecond(0).withNano(0);
                if (scheduleRepository.findByDate(localDateTime)==null) {
                    arr1.add(localDateTime);
                }
            }
            if (!arr1.isEmpty()) {
                datesTimes.put(key,arr1);
            }
        }
        return datesTimes;
    }
    public void setSchedule(Schedule schedule){
        log.info("Добавление расписания: "+schedule);
        scheduleRepository.save(schedule);

    }
    public void setTimes( ArrayList<LocalDateTime> localDateTimes, ArrayList<Long> id ){
        List<com.example.tsb.models.Service> services1=serviceRepository.findAllById(id);
        List<Schedule> schedules=new ArrayList<>();
        for (LocalDateTime l:localDateTimes){
            if(scheduleRepository.findByDate(l)==null) {
                schedules.add(new Schedule(l, services1));
            }
        }
        log.info("Добавление расписания: "+schedules);
        scheduleRepository.saveAll(schedules);
    }
    public List<Schedule> getAll(){
        List<Schedule> arr=scheduleRepository.findAll();
        Collections.sort(arr);
        return arr;
    }
//    public Schedule getByTime(LocalDateTime localDateTime){
//        return scheduleRepository.findByDate(localDateTime);
//    }
//    public Schedule getById(long id){
//        return scheduleRepository.findById(id);
//    }
    public void deleteById(long id){
        Schedule schedule=scheduleRepository.findById(id);
        if(schedule.isBooked()){
            appointmentService.delete(schedule.getAppointment().getId());
        }
        log.info("Удаление расписания: "+schedule);

        scheduleRepository.deleteById(id);


    }
    @Scheduled(fixedDelay = 60*60*1000)
    public void delSchedulesBySchedule(){
        List<Schedule> schedules=getAll();
        for(Schedule schedule:schedules){
            if(schedule.getDate().minusHours(1).minusMinutes(30).isBefore(LocalDateTime.now())){
                if(!schedule.isBooked()) {
                    deleteById(schedule.getId());
                }
                else {
                    System.out.println(schedule.getDate().plusDays(3));
                    if(schedule.getDate().plusDays(3).isBefore(LocalDateTime.now())){
                        deleteById(schedule.getId());
                    }
                }
            }
        }

    }

    public Map<String,List<Schedule>> getMapSchedules() {

        List<String> months = Arrays.asList("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек");
        Map<String, List<Schedule>> scheduleMap = new LinkedHashMap<>();

        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return null;
        }
        Collections.sort(schedules);
        int day = schedules.get(0).getDate().getDayOfMonth();
        List<Schedule> scheduleDay = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (day != schedule.getDate().getDayOfMonth()) {
                String key = day + " " + months.get(scheduleDay.get(scheduleDay.size()-1).getDate().getMonthValue() - 1);
                scheduleMap.put(key, scheduleDay);
                day = schedule.getDate().getDayOfMonth();
                scheduleDay = new ArrayList<>();
            }
            scheduleDay.add(schedule);
        }
        scheduleMap.put(day + " " + months.get(schedules.get(schedules.size() - 1).getDate().getMonthValue() - 1), scheduleDay);

        return scheduleMap;

    }
//    public void deleteInList(List<com.example.tsb.models.Service> services, Long id){
//        int i=0;
//        for(com.example.tsb.models.Service service:services){
//            if(Objects.equals(service.getId(), id)){
//                services.remove(i);
//            }
//            else {
//                i++;
//            }
//        }
//    }
    public void deleteScheduleByIdIfDeleteService(long idSchedule, long idService) {
        Schedule schedule=scheduleRepository.findById(idSchedule);
        if(schedule.isBooked() &&
                (schedule.getAppointment().getService()!=null
                        && schedule.getAppointment().getService().getId()==idService)){
            schedule.getAppointment().setService(null);
            appointmentRepository.save(schedule.getAppointment());
        }
        schedule.getServices().remove(serviceRepository.findById(idService));
        scheduleRepository.save(schedule);
    }

    public void setScheduleForService(com.example.tsb.models.Service service) {
        List<Schedule> schedules=scheduleRepository.findAll();
        for (Schedule schedule: schedules){
            schedule.getServices().add(service);
        }
        scheduleRepository.saveAll(schedules);
    }
}
