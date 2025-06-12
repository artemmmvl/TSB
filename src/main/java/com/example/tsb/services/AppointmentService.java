package com.example.tsb.services;

import com.example.tsb.configs.EmailSender;
import com.example.tsb.models.Appointment;
import com.example.tsb.models.Schedule;
import com.example.tsb.repositories.AppointmentRepository;
import com.example.tsb.repositories.ScheduleRepository;
import com.example.tsb.repositories.ServiceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private final ServiceRepository serviceRepository;

    private final EmailSender emailSender;
    private final ScheduleRepository scheduleRepository;

    public void sendEmail(Appointment appointment){
        List<String> months= Arrays.asList("января","февраля",
                "марта","апреля","мая","июня","июля","августа",
                "сентября","октября","ноября","декабря");

        String text=String.format("Добрый день, %s!🪄\n\n", appointment.getName().split(" ")[0]);
        text+=String.format("Вы записаны на %d %s в %s\n\n",
                appointment.getSchedule().getDate().getDayOfMonth(), months.get(appointment.getSchedule().getDate().getMonthValue()-1),
                appointment.getSchedule().getDate().toLocalTime());
        text+="Принимаем клиентов по адресу: \n" +
                "\n\n";
        text+="До встречи \uD83E\uDE90\n\n";
        text+="Контакты для связи:\nInst: artem.vlastyuk\ntg: artemvlastyuk";
        if(appointment.isPermissionToSendEmail()){
            emailSender.sendMessage(appointment.getEmail(),text,"Запись");
        }
        text="Новая запись:\n\n" +
                "Фио:"+appointment.getName()+
                "\nДата и время: "+
                appointment.getSchedule().getDate().toLocalDate()+" "+appointment.getSchedule().getDate().toLocalTime()+
                "\nInst: "+appointment.getInst()+
                "\nНомер телефона: "+appointment.getPhoneNumber()+
                "\nУслуга: "+appointment.getService().getName()+
                "\nКомментарий: "+appointment.getComment();
        emailSender.sendMessage("cvlastyuk@mail.ru",text,"Новая запись");
    }
    public Long setAppointment(String email, String inst, String name, String phone,
                                  Long scheduleId, String comment, Long serviceId, boolean send_email){
        Schedule schedule=scheduleRepository.getReferenceById(scheduleId);
        if(schedule!=null){
            if(schedule.isBooked()){
                return -1L;
            }
            else {
                schedule.setBooked(true);
                scheduleRepository.save(schedule);
            }
        }
        else {
            return -1L;
        }
        com.example.tsb.models.Service service=serviceRepository.getReferenceById(serviceId);
        if(service==null){
            return -1L;
        }
        Appointment appointment=new Appointment(name, email, inst, service,phone,schedule,comment, send_email);
        Appointment appointment1=appointmentRepository.save(appointment);
        log.info("Новая запись: "+ appointment1);

        sendEmail(appointment);

        return appointment1.getId();
    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }
    public Appointment getById(Long id) {
        return appointmentRepository.getReferenceById(id);
    }

    public void delete(long id) {
        Appointment appointment=appointmentRepository.findById(id);
        if(appointment!=null){
            appointment.getSchedule().setBooked(false);
        }
        log.info("Удаление записи: " + appointment);

        appointmentRepository.deleteById(id);
    }
}
