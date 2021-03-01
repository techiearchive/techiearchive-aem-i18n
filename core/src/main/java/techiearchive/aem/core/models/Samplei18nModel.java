package techiearchive.aem.core.models;

import java.util.Objects;
import java.util.Locale;
import com.adobe.granite.i18n.LocaleUtil;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Page;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;

/**
 * The Class Samplei18nModel.
 */
@Model(adaptables = {
        SlingHttpServletRequest.class }, resourceType = "techiearchive-aem/components/content/text", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class Samplei18nModel {
  
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(Samplei18nModel.class);
  
  @Self
  private SlingHttpServletRequest slingRequest;
  
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;
  
  private static final String TITLE_18NKEY = "title";

  private static final String DESC_LABEL_18NKEY = "desc";

  private static final String HELP_MESSAGE_LABEL_18NKEY = "helpMessage";
  
  private static final String TECHIEARCHIVE_AEM_I18NKEY = "techiearchive-aem";
  
  @Getter
  private String attributesJson;
  
  String title;
  
  String desc;
  
  String helpMessage;
  
  @PostConstruct
  protected void init() {
    LOGGER.debug("Inside init method.");
    Resource resource = slingRequest.getResource();
    title = geti18NLabel(TITLE_18NKEY, resource);
    desc = geti18NLabel(TITLE_18NKEY, resource);
    helpMessage = geti18NLabel(TITLE_18NKEY, resource);
    createAttributesJson();
  }
  
  private String geti18NLabel(String key, Resource resource) {
        String label = getLocaleMessage(resource, i18nProvider, TECHIEARCHIVE_AEM_I18NKEY + "." + key);
        return label;
    }
     
  private static String getLocaleMessage(Resource resource, ResourceBundleProvider i18nProvider, String messageKey) {
        String localeMessage = null;
        if (Objects.nonNull(resource) && Objects.nonNull(i18nProvider)) {
            String locale = getLocaleFromSiteRoot(resource);
            locale = StringUtils.isBlank(locale) ? getLocale(resource) : locale;
            Locale currentLocale = LocaleUtil.parseLocale(locale);
            ResourceBundle bundle = i18nProvider.getResourceBundle(currentLocale);
            localeMessage = bundle.getString(messageKey);
        }
        return localeMessage;
    }
        
   private static String getLocaleFromSiteRoot(Resource resource) {
        String siteLocale = null;
        if (Objects.nonNull(resource)) {
            String pathInfo = resource.getPath();
            Integer urlLangIndex = StringUtils.ordinalIndexOf(pathInfo, "/", 5);
            String langPagePath = pathInfo.substring(0, urlLangIndex);
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
            if (Objects.nonNull(pageManager)) {
                Page langPage = pageManager.getPage(langPagePath);
                if (Objects.nonNull(langPage)) {
                    siteLocale = langPage.getProperties().get(JcrConstants.JCR_LANGUAGE, String.class);
                }
            }
        }
        return siteLocale;
    }     
  
   private void createAttributesJson() {
        LOGGER.debug("Inside createAttributesJson method.");
        JsonObject attributeJsonMap = new JsonObject();
        attributeJsonMap.addProperty(TITLE_18NKEY, title);
        attributeJsonMap.addProperty(DESC_LABEL_18NKEY, desc);
        attributeJsonMap.addProperty(HELP_MESSAGE_LABEL_18NKEY, helpMessage);
        attributesJson = convertToJson(attributeJsonMap);
        LOGGER.debug("attributesJson created '{}'", attributesJson);
    }
   
   /**
   * convert object to json.
   */
   private static <T> String convertToJson(final T object) {
        Gson gson;
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
   }     
}
