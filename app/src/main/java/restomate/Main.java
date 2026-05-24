package restomate;

import javafx.application.Application;
import javafx.stage.Stage;
import restomate.database.DatabaseHelper;
import restomate.view.LoginView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.initializeDatabase();
        
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setTitle("RestoMate - Restaurant Management");
        primaryStage.setScene(loginView.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}