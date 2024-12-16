package ng.org.mirabilia.pms.views.modules.users;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ng.org.mirabilia.pms.services.LogService;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.MainView;
import ng.org.mirabilia.pms.views.modules.users.content.ClientContent;
import ng.org.mirabilia.pms.views.modules.users.content.StaffContent;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainView.class)
@PageTitle("Users | Property Management System")
@RolesAllowed({"ADMIN", "MANAGER", "IT_SUPPORT"})
public class UsersView extends VerticalLayout {

    private final VerticalLayout contentLayout;

    private final UserService userService;

    private final StateService stateService;

    private final UserImageService userImageService;

    private final LogService logService;
    @Autowired
    public UsersView(UserService userService, StateService stateService, UserImageService userImageService, LogService logService) {
        this.userService = userService;
        this.stateService = stateService;
        this.userImageService = userImageService;
        this.logService = logService;

        setSpacing(true);
        setPadding(false);

        contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();
        contentLayout.setSpacing(true);
        contentLayout.setPadding(false);



        Tab clientTab = new Tab("Client");
        Tab staffTab = new Tab("Staff");

        Tabs tabs = new Tabs(clientTab, staffTab);
        tabs.setWidthFull();
        tabs.addClassName("custom-tabs");
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            updateContent(selectedTab);
        });

        tabs.setSelectedTab(clientTab);
        updateContent(clientTab);

        add(tabs,contentLayout);
    }

    private void updateContent(Tab selectedTab) {
        contentLayout.removeAll();

        if (selectedTab.getLabel().equals("Client")) {
            contentLayout.add(new ClientContent(userService,stateService, userImageService,logService));
        } else if (selectedTab.getLabel().equals("Staff")) {
            contentLayout.add(new StaffContent(userService,stateService, userImageService,logService));

    }
}
}
