package academy.devdojo.youtube.course.endpoint.controller;


import academy.devdojo.youtube.core.model.Course;
import academy.devdojo.youtube.course.endpoint.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("v1/admin/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping()
    public ResponseEntity<Iterable<Course>> list(){
        return new ResponseEntity<>(courseService.list(), HttpStatus.OK);
    }
}
