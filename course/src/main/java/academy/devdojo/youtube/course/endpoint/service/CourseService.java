package academy.devdojo.youtube.course.endpoint.service;


import academy.devdojo.youtube.core.model.Course;

public interface CourseService {
    Iterable<Course> list();
}
