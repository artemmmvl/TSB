package com.example.tsb.services;

import com.example.tsb.models.Review;
import com.example.tsb.models.Schedule;
import com.example.tsb.repositories.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final ScheduleService scheduleService;
    private final ReviewService reviewService;
    @Value("${upload-directory}")
    private String UPLOAD_DIRECTORY;

    public ServiceService(ServiceRepository serviceRepository, ScheduleService scheduleService, ReviewService reviewService) {
        this.serviceRepository = serviceRepository;
        this.scheduleService = scheduleService;
        this.reviewService = reviewService;
    }
    public com.example.tsb.models.Service getByName(String name){
        return serviceRepository.getServiceByName(name);
    }
    public List<com.example.tsb.models.Service> getAll(){
        return serviceRepository.findAll();
    }
    public void save(String name, int price, List<MultipartFile> file, String description, boolean saveSchedule) throws IOException {
        com.example.tsb.models.Service service=new com.example.tsb.models.Service();
        service.setName(name);
        service.setPrice(price);
        service.setDescription(description.replaceAll("\n","<br>"));
        List<String> pathPhotos=new ArrayList<>();
        for (MultipartFile multipartFile : file) {
            String fileName = UUID.randomUUID().toString() + "." + multipartFile.getOriginalFilename();
            pathPhotos.add("img/service/"+fileName);
            File file1 = new File(UPLOAD_DIRECTORY + "img/service/" + fileName);
            multipartFile.transferTo(file1);
        }
        service.setPathMainPhoto(pathPhotos.get(0));
        service.setPathPhotos(pathPhotos);
        com.example.tsb.models.Service service1=serviceRepository.save(service);
        log.info("Добавление услуги: "+service1);
        if(saveSchedule) {
            scheduleService.setScheduleForService(service1);
        }
    }
    public void change(String name, int price, List<MultipartFile> file, String description,  Long id) throws IOException {
        com.example.tsb.models.Service service=serviceRepository.getReferenceById(id);
        if(service!=null){
            service.setName(name);
            service.setPrice(price);
            service.setDescription(description.replaceAll("\n","<br>"));

            List<String> pathPhotos=new ArrayList<>();

            for (MultipartFile multipartFile : file) {
                if(multipartFile.getSize()!=0) {
                    String fileName = UUID.randomUUID().toString() + "." + multipartFile.getOriginalFilename();
                    pathPhotos.add("img/service/"+fileName);
                    File file1 = new File(UPLOAD_DIRECTORY + "img/service/" + fileName);
                    multipartFile.transferTo(file1);
                }
            }
            if(pathPhotos.size()!=0){
                deletePhoto(service.getPathPhotos());
                service.setPathMainPhoto(pathPhotos.get(0));
                service.setPathPhotos(pathPhotos);
            }
        }
        serviceRepository.save(service);
        log.info("Изменение услуги: "+service);
    }
    public List<Schedule> getSchedulesById(long id) {
        com.example.tsb.models.Service service=serviceRepository.findById(id);
        if(service!=null) {
            return serviceRepository.findById(id).getSchedules().stream().filter(arr -> !arr.isBooked()).collect(Collectors.toList());
        }
        else {
            return null;
        }

    }
    public com.example.tsb.models.Service getById(long id){
        return serviceRepository.findById(id);
    }

    public Map<String,List<Schedule>> getMapSchedulesById(long id) {
        com.example.tsb.models.Service service = serviceRepository.findById(id);
        List<String> months=Arrays.asList("янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек");
        Map<String,List<Schedule>> scheduleMap=new LinkedHashMap<>();
        if (service != null) {
            List<Schedule> schedules = new ArrayList<>(service.getSchedules().stream().filter(arr -> !arr.isBooked()).toList());
            if(schedules.isEmpty()){
                return null;
            }
            Collections.sort(schedules);
            System.out.println(schedules.get(0).getDate().getDayOfMonth());
            int day=schedules.get(0).getDate().getDayOfMonth();
            List<Schedule> scheduleDay=new ArrayList<>();
            for (Schedule schedule : schedules) {
                System.out.println("schedule: "+schedule);
                if(day!=schedule.getDate().getDayOfMonth()) {

                    String key=day+" "+months.get(scheduleDay.get(scheduleDay.size()-1).getDate().getMonthValue()-1);
                    System.out.println(key);
                    scheduleMap.put(key,scheduleDay);
                    day=schedule.getDate().getDayOfMonth();
                    scheduleDay=new ArrayList<>();
                }
                scheduleDay.add(schedule);
            }
            scheduleMap.put(day+" "+months.get(schedules.get(schedules.size()-1).getDate().getMonthValue()-1),scheduleDay);
            return scheduleMap;

        } else {
            return null;
        }
    }

    public void deleteService(Long id) {
        com.example.tsb.models.Service service=serviceRepository.getReferenceById(id);
        List<Schedule> schedules=service.getSchedules();

        for(Schedule schedule: schedules){
            scheduleService.deleteScheduleByIdIfDeleteService(schedule.getId(), id);
        }
        List<Review> reviews=service.getReviews();
        for(Review review: reviews){
            reviewService.deleteServiceInReview(review.getId());
        }
        deletePhoto(service.getPathPhotos());
        log.info("Удаление услуги: "+serviceRepository.findById(id));
        serviceRepository.deleteById(id);


    }

    private void deletePhoto(List<String> pathPhotos) {
        for(int i=0;i<pathPhotos.size();i++){
            File file=new File(UPLOAD_DIRECTORY+pathPhotos.get(i));
            file.delete();
        }
    }
}
