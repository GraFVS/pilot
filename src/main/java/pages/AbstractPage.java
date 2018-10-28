package pages;

import factories.FieldFactory;
import fields.AbstractField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractPage {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    public Boolean isOpened() {
        AbstractField mark = FieldFactory.getPageLoadMark(this.getClass());
        LOG.debug("Загрузка страницы определяется полем {}", mark);
        return mark == null || mark.isEnabled();
    }

    public AbstractField getField(String name) {
        return FieldFactory.getField(name, this.getClass());
    }

    public List<AbstractField> getFields(String name) {
        return FieldFactory.getFields(name, this.getClass());
    }

}
