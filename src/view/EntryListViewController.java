package view;

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

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper("*****"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        validUntilColumn.setCellValueFactory(new PropertyValueFactory<>("validUntil"));

        // Ensure columns use available width
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        this.setPlaceholder(new Label("Keine Einträge gefunden."));

        this.getColumns().setAll(titleColumn, usernameColumn, passwordColumn, urlColumn, validUntilColumn);
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
