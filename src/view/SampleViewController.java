package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import model.Tag;

import javax.swing.*;

public class SampleViewController extends BorderPane {

    public SampleViewController() {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SampleView.fxml"));
        var tag = new Tag("RootTag");
        var subtag = new Tag("SubTag");
        var leaftag = new Tag("LeafTag");
        var otherLeaf = new Tag("OtherLeafTag");

        tag.getSubTags().add(subtag);
        tag.getSubTags().add(otherLeaf);
        subtag.getSubTags().add(leaftag);

        this.setCenter(new TagTree(TagTree.TreeMode.EDIT, tag));
        //this.setCenter(new TagTree(false, tag));
		/*loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
    }
}
