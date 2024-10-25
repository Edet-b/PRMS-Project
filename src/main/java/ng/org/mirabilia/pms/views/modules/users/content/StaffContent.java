package ng.org.mirabilia.pms.views.modules.users.content;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ng.org.mirabilia.pms.domain.entities.User;
import ng.org.mirabilia.pms.domain.enums.Role;
import ng.org.mirabilia.pms.services.StateService;
import ng.org.mirabilia.pms.services.UserImageService;
import ng.org.mirabilia.pms.services.UserService;
import ng.org.mirabilia.pms.views.forms.users.AddUserForm;
import ng.org.mirabilia.pms.views.forms.users.EditUserForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StaffContent extends VerticalLayout {
    private final UserService userService;
    private final StateService stateService;
    private final  UserImageService userImageService;

    private final Grid<User> userGrid;
    private final TextField searchField;
    private final ComboBox<Role> roleFilter;

    @Autowired
    public StaffContent(UserService userService, StateService stateService, UserImageService userImageService) {
        this.userService = userService;
        this.stateService = stateService;
        this.userImageService = userImageService;

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

        List<Role> staffRoles = new ArrayList<>(Arrays.stream(Role.values()).toList());
        staffRoles.removeIf((role)-> role.equals(Role.CLIENT));

        roleFilter = new ComboBox<>("Filter by Role", staffRoles);
        roleFilter.addValueChangeListener(e -> updateGrid());

        Button resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addClassName("custom-button");
        resetButton.addClassName("custom-reset-button");
        resetButton.addClassName("custom-toolbar-button");
        resetButton.addClickListener(e -> resetFilters());

        Button addUserButton = new Button("Add User");
        addUserButton.addClassName("custom-button");
        addUserButton.addClassName("custom-add-button");
        addUserButton.addClassName("custom-toolbar-button");
        addUserButton.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        userGrid = new Grid<>(User.class);
        userGrid.addClassName("custom-grid");
        userGrid.setColumns("firstName", "lastName", "email", "username", "phoneNumber");
        userGrid.addColumn(user -> user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.joining(", ")))
                .setHeader("Roles");
        userGrid.setItems(userService.getAllUsers());

        userGrid.asSingleSelect().addValueChangeListener(event -> {
            User selectedUser = event.getValue();
            if (selectedUser != null) {
                openEditUserDialog(selectedUser);
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(searchField, roleFilter, resetButton, addUserButton);
        toolbar.setWidthFull();
        toolbar.addClassName("custom-toolbar");

        add(toolbar, userGrid);

        addUserButton.addClickListener(e -> openAddUserDialog());

        updateGrid();
    }

    private void updateGrid() {
        String keyword = searchField.getValue();
        Role selectedRole = roleFilter.getValue();

        List<User> users = userService.searchUsersByFilters(keyword, selectedRole);
        //remove users with CLIENT ROLE
        users.removeIf((user -> user.getRoles().contains(Role.CLIENT)));
        userGrid.setItems(users);
    }

    private void resetFilters() {
        searchField.clear();
        roleFilter.clear();
        updateGrid();
    }

    private void openAddUserDialog() {
        AddUserForm userForm = new AddUserForm(userService,stateService, userImageService,(v) -> updateGrid());
        userForm.open();
    }

    private void openEditUserDialog(User user) {
        EditUserForm editUserForm = new EditUserForm(userService, userImageService, user, (v) -> updateGrid());
        editUserForm.open();
    }
}