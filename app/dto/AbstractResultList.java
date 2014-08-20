package dto;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import annotations.PageSize;

/**
 * Created by Marceloo on 11/07/2014.
 */
public abstract class AbstractResultList<E> implements ResultList<E>{

    private Long page = 0L;

    private final Long count;

    private Long pageCount;

    private final Criteria criteria;

    public Criteria getCriteria() {
        return criteria;
    }

    public Long getPage() {
        return page;
    }

    @Override
	public void setPage(final Long page) {
        if (page != null && page > 0L) {
            this.page = page;
        }
    }

    @Override
    public Long getCount() {
        return count;
    }

    @Override
    public Long getPageCount() {
        return pageCount;
    }



    public AbstractResultList(final Criteria criteria) {
        this.count = (Long) criteria.setProjection(Projections.count("id"))
                .uniqueResult();
        criteria.setProjection(null);
        criteria.setMaxResults(this.getPageSize());
        this.criteria = criteria;
        this.pageCount = (count / 5);

        if (count % 5 > 0) {
            this.pageCount++;
        }
    }

    @Override
    public boolean next() {

        if ( page < this.getPageCount() ) {
            this.page++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<E> list() {
        return this.getCriteria().setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .setFirstResult(this.getPage().intValue() * this.getPageSize())
                .list();
    }

    public int getPageSize() {

        final Class clazz = this.getClass();

        if( !clazz.isAnnotationPresent(PageSize.class) ) {
            return 10;
        } else {
            PageSize pagesize = (PageSize)clazz.getAnnotation(PageSize.class);
            return pagesize.value();
        }
    }
}
