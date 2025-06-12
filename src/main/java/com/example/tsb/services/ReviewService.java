package com.example.tsb.services;

import com.example.tsb.models.Review;
import com.example.tsb.repositories.ReviewRepository;
import com.example.tsb.repositories.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    @Value("${upload-directory}")
    private String UPLOAD_DIRECTORY;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, ServiceRepository serviceRepository, UserService userService1) {
        this.reviewRepository = reviewRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService1;
    }
    public void saveReview(byte stars, String name, String text, List<MultipartFile> files, String service) throws IOException {
        Review review=new Review();
        review.setReviewText(text.replaceAll("\n","<br>"));
        review.setStars(stars);
        review.setName(name);
        review.setService(serviceRepository.findById(Long.parseLong(service)));
        List<String> paths=new ArrayList<>();

        for(var file:files){
            if(file.getSize()!=0) {
                String fileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
                paths.add("img/reviews/" + fileName);
                File file1 = new File(UPLOAD_DIRECTORY + "img/reviews/" + fileName);
                file.transferTo(file1);
            }
        }
        review.setPathImages(paths);
        Review review1=reviewRepository.save(review);
        log.info("Добавление отзыва: "+review1);

    }
    public List<Review> getAllReviewsByConfirm(boolean confirm){
        List<Review> reviews=reviewRepository.findAllByConfirmation(confirm);
        Collections.reverse(reviews);
        return reviews;
    }
    private void deletePhoto(List<String> pathPhotos) {
        for(int i=0;i<pathPhotos.size();i++){
            File file=new File(UPLOAD_DIRECTORY+pathPhotos.get(i));
            file.delete();
        }
    }
    public void deleteReview(long id){
        Review review=reviewRepository.findById(id);
        deletePhoto(review.getPathImages());
        log.info("Удаление отзыва" + review);
        reviewRepository.deleteById(id);
    }

    public void accept(long id) {
        Review review=reviewRepository.findById(id);
        review.setConfirmation(true);
        log.info("Принятие отзыва: "+ review);
        reviewRepository.save(review);
    }

    public void deleteServiceInReview(long idReview) {
        Review review=reviewRepository.getReferenceById(idReview);
        review.setService(null);
        reviewRepository.save(review);
    }
}
