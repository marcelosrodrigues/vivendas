package dto;

/**
 * Created by Marceloo on 11/07/2014.
 */
public abstract class AbstractResultList<E> implements ResultList<E>{

    private Long page = 0L;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) { this.page = page; }

    @Override
    public boolean next() {

        if ( page < this.getPageCount() ) {
            this.page++;
            return true;
        } else {
            return false;
        }

    }

}
