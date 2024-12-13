package ng.org.mirabilia.pms.views.modules.users.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.LogService;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.users.AddUserForm;
import ng.org.mirabilia.pms.views.forms.users.EditUserForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ClientContent extends VerticalLayout {

    private final StateService stateService;
    private final UserService userService;
    UserImageService userImageService;

    LogService logService;
    private final Grid<User> userGrid;
    private final TextField searchField;

    @Autowired
    public ClientContent(UserService userService, StateService stateService, UserImageService userImageService, LogService logService) {
        this.userService = userService;
        this.stateService = stateService;
        this.userImageService = userImageService;
        this.logService = logService;

        setSpacing(true);
        setPadding(false);
        addClassName("user-content");

        searchField = new TextField();
        searchField.setPlaceholder("Search User");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("custom-search-field");
        searchField.addClassName("custom-toolbar-field");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateGrid());

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClassName("custom-button");
        resetButton.addClassName("custom-reset-button");
        resetButton.addClassName("custom-toolbar-button");
        resetButton.addClickListener(e -> resetFilters());

        Button addUserButton = new Button("Add Client");
        addUserButton.addClassName("custom-button");
        addUserButton.addClassName("custom-add-button");
        addUserButton.addClassName("custom-toolbar-button");
        addUserButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        userGrid = new Grid<>(User.class, false);
        userGrid.addClassName("custom-grid");

        Grid.Column<User> emailColumn =   userGrid.addColumn(User::getEmail).setHeader("E-mail");
        emailColumn.setSortable(true);
        Grid.Column<User> usernameColumn =   userGrid.addColumn(User::getUsername).setHeader("Username");
        usernameColumn.setSortable(true);
        Grid.Column<User> phoneNumberColumn =   userGrid.addColumn(User::getPhoneNumber).setHeader("Phone number");
        phoneNumberColumn.setSortable(true);

        Grid.Column<User> statusColumn =   userGrid.addColumn(
                new ComponentRenderer<>(user->{
                    Span statusSpan;
                    if(user.isActive()){
                        statusSpan = new Span("Active");
                        statusSpan.getStyle().setColor("green");
                    }else{
                        statusSpan = new Span("Inactive");
                        statusSpan.getStyle().setColor("red");
                    }
                    return statusSpan;
                }
                )).setHeader("Status");
        statusColumn.setSortable(true);

        Grid.Column<User> codeColumn = userGrid.addColumn((user)->user.getUserCode()
                )
                .setHeader("User Code");

        Grid.Column<User> nameColumn = userGrid.addColumn((user)->user.getFirstName() + " " + user.getLastName()
                )
                .setHeader("FullName");
        nameColumn.setSortable(true);
        Grid.Column<User> roleColumn = userGrid.addColumn(user -> user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.joining(", ")))
                .setHeader("User Role");
        roleColumn.setSortable(true);
        Grid.Column<User> locationColumn = userGrid.addColumn(User::getState)
                .setHeader("Location");
        locationColumn.setSortable(true);

        userGrid.setColumnOrder(codeColumn,nameColumn,usernameColumn, roleColumn,statusColumn,locationColumn,phoneNumberColumn, emailColumn);

        userGrid.setItems(userService.getAllUsers());

        userGrid.asSingleSelect().addValueChangeListener(event -> {
            User selectedUser = event.getValue();
            if (selectedUser != null) {
                openEditUserDialog(selectedUser);
            }
        });



        HorizontalLayout toolbar = new HorizontalLayout(searchField, resetButton, addUserButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");

        add(toolbar, userGrid);

        addUserButton.addClickListener(e -> openAddUserDialog());

        updateGrid();
    }

    private void updateGrid() {
        String keyword = searchField.getValue();
        Role selectedRole = Role.CLIENT;

        List<User> users = userService.searchUsersByFilters(keyword, selectedRole);
        userGrid.setItems(users);
    }

    private void resetFilters() {
        searchField.clear();
        updateGrid();
    }

    private void openAddUserDialog() {
        AddUserForm userForm = new AddUserForm(userService,stateService, userImageService,logService,(v) -> updateGrid(),Role.CLIENT);
        userForm.open();
    }

    private void openEditUserDialog(User user) {
        EditUserForm editUserForm = new EditUserForm(userService,userImageService, logService,user, (v) -> updateGrid(), Role.CLIENT);
        editUserForm.open();
    }
}
