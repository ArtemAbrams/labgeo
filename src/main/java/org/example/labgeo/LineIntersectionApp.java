package org.example.labgeo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LineIntersectionApp extends Application {

    private Canvas canvas;
    private GraphicsContext gc;
    private final Random random = new Random();
    private final List<Point> userPoints = new ArrayList<>();
    private final List<Segment> userSegments = new ArrayList<>();
    private XYSeries series = new XYSeries("Execution Time");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Line Intersection App");

        Label label = new Label("Enter the number of lines:");
        TextField textField = new TextField();
        Button drawButton = new Button("Draw Random Lines");
        Button clearButton = new Button("Clear Canvas");
        Button drawUserLinesButton = new Button("Draw User Lines");
        Button showChartButton = new Button("Show Chart");

        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        drawButton.setOnAction(e -> {
            String text = textField.getText();
            try {
                int numLines = Integer.parseInt(text);
                if (numLines > 0 && numLines <= 10000) {
                    drawRandomLines(numLines);
                } else {
                    showAlert("Please enter a positive integer less than or equal to 10000.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid number. Please enter an integer.");
            }
        });

        clearButton.setOnAction(e -> clearCanvas());

        drawUserLinesButton.setOnAction(e -> {
            String text = textField.getText();
            try {
                int numLines = Integer.parseInt(text);
                if (numLines > 0 && numLines <= 100) {
                    showUserInputWindow(numLines);
                } else {
                    showAlert("Please enter a positive integer less than or equal to 100.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid number. Please enter an integer.");
            }
        });

        showChartButton.setOnAction(e -> showChart());

        HBox buttonBox = new HBox(10, drawButton, drawUserLinesButton, clearButton, showChartButton);
        VBox root = new VBox(10, label, textField, buttonBox, canvas);

        Scene scene = new Scene(root, 800, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawRandomLines(int numLines) {
        long startTime = System.currentTimeMillis();  // Початок вимірювання часу

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCoordinateAxes();

        List<Segment> segments = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            double x1 = random.nextDouble() * canvas.getWidth();
            double y1 = random.nextDouble() * canvas.getHeight();
            double x2 = random.nextDouble() * canvas.getWidth();
            double y2 = random.nextDouble() * canvas.getHeight();
            Point p1 = new Point(x1, y1);
            Point p2 = new Point(x2, y2);
            Segment segment = new Segment(p1, p2);
            segments.add(segment);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(x1, y1, x2, y2);
        }

        BentleyOttmann algorithm = new BentleyOttmann(new ArrayList<>(segments));
        algorithm.findIntersections();
        List<Point> intersections = algorithm.getIntersections();

        gc.setFill(Color.RED);
        for (Point p : intersections) {
            gc.fillOval(p.getXCoord() - 3, p.getYCoord() - 3, 6, 6);
        }

        long endTime = System.currentTimeMillis();  // Кінець вимірювання часу
        long duration = endTime - startTime;  // Обчислення тривалості
        series.add(numLines, duration);
        showAlert("Time to draw and compute random lines: " + duration + " ms");
    }

    private void drawUserLines() {
        long startTime = System.currentTimeMillis();  // Початок вимірювання часу

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCoordinateAxes();

        gc.setStroke(Color.BLACK);
        for (Segment segment : userSegments) {
            gc.strokeLine(segment.first().getXCoord(), segment.first().getYCoord(), segment.second().getXCoord(), segment.second().getYCoord());
        }

        BentleyOttmann algorithm = new BentleyOttmann(userSegments);
        algorithm.findIntersections();
        List<Point> intersections = algorithm.getIntersections();

        gc.setFill(Color.RED);
        for (Point p : intersections) {
            gc.fillOval(p.getXCoord() - 3, p.getYCoord() - 3, 6, 6);
        }

        long endTime = System.currentTimeMillis();  // Кінець вимірювання часу
        long duration = endTime - startTime;  // Обчислення тривалості
        series.add(userSegments.size(), duration);
        showAlert("Time to draw and compute user lines: " + duration + " ms");
    }

    private void showUserInputWindow(int numLines) {
        Stage inputStage = new Stage();
        inputStage.setTitle("Enter Points for Lines");
        inputStage.initModality(Modality.APPLICATION_MODAL);

        VBox inputRoot = new VBox(10);
        List<TextField> pointFields = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            TextField pointField = new TextField();
            pointField.setPromptText("x1,y1,x2,y2");
            pointFields.add(pointField);
            inputRoot.getChildren().add(pointField);
        }
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            try {
                userSegments.clear();
                for (TextField pointField : pointFields) {
                    String[] coords = pointField.getText().split(",");
                    if (coords.length == 4) {
                        double x1 = Double.parseDouble(coords[0].trim());
                        double y1 = Double.parseDouble(coords[1].trim());
                        double x2 = Double.parseDouble(coords[2].trim());
                        double y2 = Double.parseDouble(coords[3].trim());
                        addUserPoint(new Point(x1, y1));
                        addUserPoint(new Point(x2, y2));
                    } else {
                        showAlert("Please enter coordinates in the format x1,y1,x2,y2.");
                        return;
                    }
                }
                inputStage.close();
                drawUserLines();
            } catch (NumberFormatException ex) {
                showAlert("Invalid coordinates. Please enter numbers.");
            }
        });

        inputRoot.getChildren().add(submitButton);
        Scene inputScene = new Scene(inputRoot, 400, 400);
        inputStage.setScene(inputScene);
        inputStage.showAndWait();
    }

    private void addUserPoint(Point point) {
        userPoints.add(point);
        if (userPoints.size() == 2) {
            Segment segment = new Segment(userPoints.get(0), userPoints.get(1));
            userSegments.add(segment);
            userPoints.clear();
        }
    }

    private void drawCoordinateAxes() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double midX = width / 2;
        double midY = height / 2;

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

        // Draw X axis
        gc.strokeLine(0, midY, width, midY);

        // Draw Y axis
        gc.strokeLine(midX, 0, midX, height);

        // Draw X axis labels
        for (int i = 0; i < width; i += 50) {
            gc.strokeLine(i, midY - 5, i, midY + 5);
        }

        // Draw Y axis labels
        for (int i = 0; i < height; i += 50) {
            gc.strokeLine(midX - 5, i, midX + 5, i);
        }
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        userPoints.clear();
        userSegments.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showChart() {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Time Dependency on Number of Vertices",
                "Number of Vertices",
                "Execution Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Execution Time Chart");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}