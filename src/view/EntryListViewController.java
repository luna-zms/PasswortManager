package view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Entry;

public class EntryListViewController extends TableView<Entry> {
    // TODO: Maybe replace this with ObjectProperty<Tag> since general filters are supposed to be handled by the controller
    private ObjectProperty<ObservableList<Entry>> entries = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Entry>> filter = new SimpleObjectProperty<>();

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

        // Entry setting and filtering
        entries.addListener((obs, oldEntries, newEntries) -> applyFilter());
        filter.addListener((obs, oldPred, newPred) -> applyFilter());
    }

    public ObjectProperty<Predicate<Entry>> filterProperty() {
        return filter;
    }

    public void setEntries(ObservableList<Entry> entries) {
        this.entries.set(entries);
    }

    private void applyFilter() {
        ObservableList<Entry> newEntries = entries.getValue();
        Predicate<Entry> newFilter = filter.getValue();

        if (newFilter != null && newEntries != null) setItems(newEntries.filtered(newFilter));
        else if (entries != null) setItems(newEntries);
        else setEntries(FXCollections.emptyObservableList());
    }
}
