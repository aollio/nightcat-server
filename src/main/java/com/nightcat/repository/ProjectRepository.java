package com.nightcat.repository;

import com.nightcat.entity.Project;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ProjectRepository extends AbstractRepository<Project> {


    public List<Project> findByType(String type, int limit, Timestamp since_time, Timestamp max_time) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session
                .createQuery(
                        "From Project p where (p.type like :typestr) and (p.create_time between :low and :high)"
                );

        query.setTimestamp("low", since_time);
        query.setTimestamp("high", max_time);
        query.setString("typestr", type);

        query.setFirstResult(0);
        query.setMaxResults(limit);

        List<Project> projects = query.list();
        return projects;
    }
}
