package view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Entry;

public class EntryListViewController extends TableView<Entry> {
    private Predicate<Entry> filter;
    private ObservableList<Entry> entries;

    public EntryListViewController() {
        TableColumn<Entry, String> titleColumn = new TableColumn<>("Titel");
        TableColumn<Entry, String> usernameColumn = new TableColumn<>("Nutzername");
        TableColumn<Entry, String> passwordColumn = new TableColumn<>("Passwort");
        TableColumn<Entry, String> urlColumn = new TableColumn<>("URL");
        TableColumn<Entry, String> validUntilColumn = new TableColumn<>("Gültig bis");

        // Bind columns to getters of Entry
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper("*****"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        // Use getter with specific string formatting
        validUntilColumn.setCellValueFactory(new PropertyValueFactory<>("validUntilString"));

        // Right-align date column
        validUntilColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Ensure columns use available width
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        // Make placeholder German
        setPlaceholder(new Label("Keine Einträge gefunden."));

        // Make Java happy by specifying the exact types
        List<TableColumn<Entry, String>> columns = new ArrayList<>();
        columns.add(titleColumn);
        columns.add(usernameColumn);
        columns.add(passwordColumn);
        columns.add(urlColumn);
        columns.add(validUntilColumn);
        getColumns().setAll(columns);
    }


    public void setFilter(Predicate<Entry> filter) {
        this.filter = filter;
        applyFilter();
    }


    public void setEntries(ObservableList<Entry> entries) {
        this.entries = entries;
        applyFilter();
    }

    private void applyFilter() {
        if (filter != null && entries != null) setItems(entries.filtered(filter));
        else if (entries != null) setItems(entries);
    }
}
