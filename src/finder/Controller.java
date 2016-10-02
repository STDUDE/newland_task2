package finder;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.*;

import static java.util.Calendar.*;
import static javafx.collections.MapChangeListener.*;

public class Controller  implements Initializable {
    @FXML private ComboBox dayOfWeek;

    @FXML private Label result;
    @FXML private Label nearest;

    @FXML private TextField dayNumber;

    @FXML private DatePicker referenceDate;


    ObservableList<String> options;
    DayOfWeek day;

    Alert alert = new Alert(Alert.AlertType.ERROR);
    String message;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        options = FXCollections.observableArrayList(
                "понедельник",
                "вторник",
                "среда",
                "четверг",
                "пятница",
                "суббота",
                "воскресенье"
        );
        dayOfWeek.setItems(options);
        dayOfWeek.valueProperty().addListener(changeDayOfWeek());
        dayOfWeek.setValue(options.get(0));
        dayNumber.addEventFilter(KeyEvent.KEY_TYPED , numericValidation());
        referenceDate.setValue(LocalDate.now());
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка");

    }

    public EventHandler<KeyEvent> numericValidation() {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txt_TextField = (TextField) e.getSource();
                if(!e.getCharacter().matches("[0-9]")) {
                    e.consume();
                }
            }
        };
    }

    public ChangeListener<String> changeDayOfWeek(){
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                if (options.get(0).equals(t1) || options.get(1).equals(t1) || options.get(3).equals(t1)) {
                    nearest.setText("Ближайший");
                } else if (options.get(2).equals(t1) || options.get(4).equals(t1) || options.get(5).equals(t1)) {
                    nearest.setText("Ближайшая");
                } else {
                    nearest.setText("Ближайшее");
                }
                toInt(t1);
            }
        };
    }

    private boolean checkDay() {
        try {
            if(Integer.parseInt(dayNumber.getText()) < 0 || Integer.parseInt(dayNumber.getText()) > 31) {
                    throw new DataException("Неправильное значение дня!");
            }

        } catch (DataException e) {
            alert.setContentText(e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            alert.setContentText("Пустое поле дня!");
            return false;
        }
        return true;
    }

    private boolean checkDate() {
        try {
            if(referenceDate.getValue() == null) {
                throw new DataException("Пустое поле даты!");
            }
        } catch (DataException e) {
            alert.setContentText(e.getMessage());
            return false;
        }
        return true;
    }

    public void findDate(ActionEvent actionEvent) {
        if(!(checkDay() && checkDate())) {
            alert.showAndWait();
            return;
        }
        LocalDate localDate = referenceDate.getValue();

        localDate = localDate.plusDays(dayRange(localDate.getDayOfWeek().getValue(), day.getValue()));
        while(true) {
            if(Integer.parseInt(dayNumber.getText()) == localDate.getDayOfMonth()) {
                break;
            }
            localDate = localDate.plusDays(7);
        }

        parseToCron(localDate);
    }

    private void parseToCron(LocalDate localDate) {
        //<Минуты> <Часы> <Дни_месяца> <Месяцы> <Дни_недели> <Годы>

        DateTimeFormatter month = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("en"));
        DateTimeFormatter day = DateTimeFormatter.ofPattern("E", Locale.forLanguageTag("en"));
        String monthName = localDate.format(month);
        String dayName = localDate.format(day);
        StringBuilder str = new StringBuilder();
        str.append("* ").append("* ");
        str.append(localDate.getDayOfMonth()).append(" ");
        str.append(monthName).append(" ");
        str.append(dayName).append(" ");
        str.append(localDate.getYear()).append(" ");
        result.setText(str.toString());
    }

    private int dayRange(int firstDay, int lastDay){
        if(firstDay < lastDay) {
            return (lastDay - firstDay);
        } else if (firstDay > lastDay) {
            return (7 - firstDay + lastDay);
        } else return 0;
    }

    private void toInt(String dayOfWeek) {
        switch(dayOfWeek) {
            case "понедельник":
                day = DayOfWeek.MONDAY; break;
            case "вторник":
                day = DayOfWeek.TUESDAY; break;
            case "среда":
                day = DayOfWeek.WEDNESDAY; break;
            case "четверг":
                day = DayOfWeek.THURSDAY; break;
            case "пятница":
                day = DayOfWeek.FRIDAY; break;
            case "суббота":
                day = DayOfWeek.SATURDAY; break;
            case "воскресенье":
                day = DayOfWeek.SUNDAY; break;
        }
    }

    public void selectAll(Event event) {
        dayNumber.selectAll();
    }
}
