package academy.devdojo.youtube.course.endpoint.service;


import academy.devdojo.youtube.core.model.Course;
import academy.devdojo.youtube.core.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {
    private CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    @Override
    public Iterable<Course> list() {
        return courseRepository.findAll();
    }
}
