package dto;

import models.Escritura;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import java.util.List;

/**
 * Created by Marceloo on 11/07/2014.
 */
public class EscrituraResultList extends AbstractResultList<Escritura> {

    private final Long count;
    private Long pageCount;
    private final Criteria criteria;

    public EscrituraResultList(final Criteria criteria) {

        this.count = (Long) criteria.setProjection(Projections.count("id")).uniqueResult();
        criteria.setProjection(null);
        this.pageCount = (count / 5);

        if (pageCount % 5 > 0) {
            this.pageCount++;
        }

        this.criteria = criteria;
        this.criteria.setMaxResults(5);
    }

    @Override
    public Long getCount() {
        return count;
    }

    @Override
    public Long getPageCount() {
        return pageCount;
    }

    @Override
    public List<Escritura> list() {
        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .setFirstResult(super.getPage().intValue() * 5)
                .list();
    }
}
