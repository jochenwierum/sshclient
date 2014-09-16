package de.jowisoftware.sshclient.log;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "Panel", category = "Core", elementType = "appender", printObject = true)
public class PanelAppender extends AbstractAppender {

    protected PanelAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout) {
        super(name, filter, layout, false);
    }

    @Override
    public void append(final LogEvent event) {
        final String message = new String(getLayout().toByteArray(event));
        LogObserver.getInstance().triggerLog(new LogMessageContainer(message));
    }

    @PluginFactory
    public static PanelAppender createAppender(@PluginAttribute("name") final String name,
                                              @PluginElement("Layout") Layout<?> layout,
                                              @PluginElement("Filters") final Filter filter) {

        if (name == null) {
            System.out.println("No name provided for StubAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new PanelAppender(name, filter, layout);
    }
}
