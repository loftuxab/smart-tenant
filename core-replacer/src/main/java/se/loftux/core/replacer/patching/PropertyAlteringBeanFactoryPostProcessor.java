package se.loftux.core.replacer.patching;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;

/**
 * Bean post processor to alter a property of a bean definition with adapted configuration before instantiation without requiring an
 * override that may conflict with custom Spring configuration.
 *
 * @author Axel Faust
 */
public class PropertyAlteringBeanFactoryPostProcessor implements BeanFactoryPostProcessor
{

    protected String targetBeanName;

    protected boolean active;

    protected String propertyName;

    protected String beanReferenceName;

    protected List<String> beanReferenceNames;

    // can only handle simple values, no maps (except via bean references)

    protected Object value;

    protected List<Object> values;

    protected boolean merge;

    protected boolean mergeParent;

    /**
     * @param targetBeanName
     *            the targetBeanName to set
     */
    public void setTargetBeanName(final String targetBeanName)
    {
        this.targetBeanName = targetBeanName;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(final boolean active)
    {
        this.active = active;
    }

    /**
     * @param propertyName
     *            the propertyName to set
     */
    public void setPropertyName(final String propertyName)
    {
        this.propertyName = propertyName;
    }

    /**
     * @param beanReferenceName
     *            the beanReferenceName to set
     */
    public void setBeanReferenceName(final String beanReferenceName)
    {
        this.beanReferenceName = beanReferenceName;
    }

    /**
     * @param beanReferenceNames
     *            the beanReferenceNames to set
     */
    public void setBeanReferenceNames(final List<String> beanReferenceNames)
    {
        this.beanReferenceNames = beanReferenceNames;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final Object value)
    {
        this.value = value;
    }

    /**
     * @param values
     *            the values to set
     */
    public void setValues(final List<Object> values)
    {
        this.values = values;
    }

    /**
     * @param merge
     *            the merge to set
     */
    public void setMerge(final boolean merge)
    {
        this.merge = merge;
    }

    /**
     * @param mergeParent
     *            the mergeParent to set
     */
    public void setMergeParent(final boolean mergeParent)
    {
        this.mergeParent = mergeParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        if (this.active && this.targetBeanName != null && this.propertyName != null)
        {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(this.targetBeanName);
            if (beanDefinition != null)
            {
                final MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
                final PropertyValue configuredValue = propertyValues.getPropertyValue(this.propertyName);

                final Object value;

                if (this.values != null || this.beanReferenceNames != null)
                {
                    final ManagedList<Object> list = new ManagedList<>();

                    if (this.merge && configuredValue != null)
                    {
                        final Object configuredValueDefinition = configuredValue.getValue();
                        if (configuredValueDefinition instanceof ManagedList<?>)
                        {
                            final ManagedList<?> oldList = (ManagedList<?>) configuredValueDefinition;
                            list.setElementTypeName(oldList.getElementTypeName());
                            list.setMergeEnabled(oldList.isMergeEnabled());
                            list.setSource(oldList.getSource());

                            list.addAll(oldList);
                        }
                    }

                    if (this.values != null)
                    {
                        list.addAll(this.values);
                    }
                    else
                    {
                        for (final String beanReferenceName : this.beanReferenceNames)
                        {
                            list.add(new RuntimeBeanReference(beanReferenceName));
                        }
                    }

                    list.setMergeEnabled(list.isMergeEnabled() || this.mergeParent);
                    value = list;
                }
                else if (this.value != null)
                {
                    value = this.value;
                }
                else if (this.beanReferenceName != null)
                {
                    value = new RuntimeBeanReference(this.beanReferenceName);
                }
                else
                {
                    value = null;
                }

                if (value != null)
                {
                    final PropertyValue newValue = new PropertyValue(this.propertyName, value);
                    propertyValues.addPropertyValue(newValue);
                }
                else if (configuredValue != null)
                {
                    propertyValues.removePropertyValue(configuredValue);
                }
            }
        }

    }
}