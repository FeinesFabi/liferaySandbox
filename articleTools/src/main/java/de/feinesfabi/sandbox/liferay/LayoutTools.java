package de.feinesfabi.sandbox.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.journal.model.JournalArticle;

import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import java.io.IOException;

import static com.liferay.portal.util.PortletKeys.JOURNAL_CONTENT;

public class LayoutTools {
    final static boolean CHECK_PERMISSIONS = false;
    final static int DEFAULT_COLUMN = 1;
    final static int DEFAULT_COLUMN_POS = -1;

    final static String DEFAULT_PORTLET_ID = JOURNAL_CONTENT;


    public static void addLiveArticleToLayout(Layout layout, JournalArticle article, long userId) {
        addLiveArticleToLayout(layout, DEFAULT_PORTLET_ID, article, userId);
    }

    public static void addLiveArticleToLayout(Layout layout, String portletId, JournalArticle article, long userId) {
        addLiveArticleToLayout(layout, DEFAULT_PORTLET_ID, article, userId, "column-" + DEFAULT_COLUMN, DEFAULT_COLUMN_POS);
    }

    public static void addLiveArticleToLayout(Layout layout, String portletId, JournalArticle article, long userId, String column, int columnPos) {
        String portletInstanceId = addPortletId(userId, layout, portletId, column, columnPos);
        configureJournalContent(layout, portletInstanceId, article.getArticleId());
    }

    private static void configureJournalContent(Layout layout, String portletId, String articleId) {
        javax.portlet.PortletPreferences portletSetup = null;

        try {
            portletSetup = PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletId);
            portletSetup.setValue("groupId", String.valueOf(layout.getGroupId()));
            portletSetup.setValue("articleId", articleId);

            portletSetup.store();

        } catch (SystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidatorException e) {
            e.printStackTrace();
        } catch (ReadOnlyException e) {
            e.printStackTrace();
        }

    }

    protected static String addPortletId (long userId, Layout layout, String portletId, String columnId, int columnPos) {
        LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();

        try {

            portletId = layoutTypePortlet.addPortletId(userId, portletId, columnId, columnPos, CHECK_PERMISSIONS);
            LayoutLocalServiceUtil.updateLayout(layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(), layout.getTypeSettings());

        } catch (PortalException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();
        }


        return portletId;
    }

    protected static String addPortletId (long userId, Layout layout, String portletId, String columnId) {
        return addPortletId(userId, layout, portletId, "column-" + columnId, DEFAULT_COLUMN_POS);
    }

    protected static String addPortletId (long userId, Layout layout, String portletId) {
        return addPortletId(userId, layout, portletId, "column-" + DEFAULT_COLUMN, DEFAULT_COLUMN_POS);
    }
}
