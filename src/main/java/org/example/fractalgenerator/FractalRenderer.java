package org.example.fractalgenerator;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Parent;  // Add this at the top with other imports
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;

// JavaFX application to render fractals
public class FractalRenderer extends Application {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final double MIN_ZOOM = 1e-3;
    private static final double MAX_ZOOM = 1e3;

    // Fractal rendering variables
    private double zoomFactor = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    // Position of the mouse when dragging
    private double startDragX, startDragY;
    private double prevOffsetX, prevOffsetY;
    private Color customColor = Color.BLACK;
    // Image for Mandelbrot/Julia sets
    private WritableImage fractalImage;
    private Text statusText = new Text();
    private ColorPicker backgroundPicker = new ColorPicker(Color.WHITE);
    private ColorPicker insideColorPicker = new ColorPicker(Color.BLACK);

    // Launch the JavaFX application
    @Override
    public void start(Stage primaryStage) {
        ChoiceBox<String> fractalTypeChoice = new ChoiceBox<>();
        fractalTypeChoice.getItems().addAll("Sierpinski Triangle", "Mandelbrot Set", "Julia Set", "Koch Snowflake");
        fractalTypeChoice.setValue("Sierpinski Triangle");

        // Create text fields and choice box
        TextField depthField = new TextField("6");
        ChoiceBox<String> colorSchemeChoice = new ChoiceBox<>();
        colorSchemeChoice.getItems().addAll("Monochrome", "Rainbow", "Custom");
        colorSchemeChoice.setValue("Monochrome");

        // Create color pickers and text fields
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        TextField juliaCXField = new TextField("-0.7");
        TextField juliaCYField = new TextField("0.27015");

        // Create buttons
        Button drawButton = new Button("Draw Fractal");
        Button saveButton = new Button("Save Image");
        Button resetButton = new Button("Reset View");
        Button mandelbrotPreset = new Button("Mandelbrot Preset");
        Button juliaPreset = new Button("Julia Preset");
        ToggleButton themeToggle = new ToggleButton("Dark Mode");

        // Create input grid
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(8);
        inputGrid.setHgap(8);
        // Align the grid in the center
        inputGrid.addRow(0, new Label("Fractal Type:"), fractalTypeChoice);
        inputGrid.addRow(1, new Label("Depth/Iterations:"), depthField);
        inputGrid.addRow(2, new Label("Color Scheme:"), colorSchemeChoice);
        inputGrid.addRow(3, new Label("Background Color:"), backgroundPicker);
        inputGrid.addRow(4, new Label("Inside Color:"), insideColorPicker);
        inputGrid.addRow(5, new Label("Custom Color:"), colorPicker);
        inputGrid.addRow(6, new Label("Julia cX:"), juliaCXField);
        inputGrid.addRow(7, new Label("Julia cY:"), juliaCYField);
        // Align the buttons in the center
        inputGrid.add(drawButton, 0, 8, 2, 1);
        inputGrid.add(resetButton, 0, 9, 2, 1);
        inputGrid.add(saveButton, 0, 10, 2, 1);
        inputGrid.add(mandelbrotPreset, 0, 11, 2, 1);
        inputGrid.add(juliaPreset, 0, 12, 2, 1);
        inputGrid.add(themeToggle, 0, 13, 2, 1);

        // Create canvas and layout
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        HBox statusBar = new HBox(statusText);
        statusBar.setPadding(new Insets(5));
        VBox layout = new VBox(10, inputGrid, canvas, statusBar);
        Scene scene = new Scene(layout, WIDTH, HEIGHT + 250);

        // Disable custom color picker for non-custom color schemes
        colorPicker.disableProperty().bind(Bindings.createBooleanBinding(() ->
                !"Custom".equals(colorSchemeChoice.getValue()), colorSchemeChoice.valueProperty()));

        // Disable Julia set fields for non-Julia sets
        juliaCXField.disableProperty().bind(Bindings.createBooleanBinding(() ->
                !"Julia Set".equals(fractalTypeChoice.getValue()), fractalTypeChoice.valueProperty()));
        juliaCYField.disableProperty().bind(juliaCXField.disableProperty());

        // Disable background color picker for Mandelbrot/Julia sets
        backgroundPicker.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        !(fractalTypeChoice.getValue().equals("Sierpinski Triangle") ||
                                fractalTypeChoice.getValue().equals("Koch Snowflake")),
                fractalTypeChoice.valueProperty()));

        // Disable inside color picker for non-Mandelbrot/Julia sets
        insideColorPicker.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        !(fractalTypeChoice.getValue().equals("Mandelbrot Set") ||
                                fractalTypeChoice.getValue().equals("Julia Set")),
                fractalTypeChoice.valueProperty()));

        // Event handlers for buttons
        drawButton.setOnAction(e -> redrawFractal(gc, fractalTypeChoice, depthField,
                colorSchemeChoice, colorPicker, juliaCXField, juliaCYField));

        // Reset the view to default settings
        resetButton.setOnAction(e -> resetView(gc, fractalTypeChoice, depthField,
                colorSchemeChoice, colorPicker, juliaCXField, juliaCYField));

        // Save the image to a file
        saveButton.setOnAction(e -> saveImage(canvas, primaryStage));

        // Set Mandelbrot set preset values
        mandelbrotPreset.setOnAction(e -> {
            zoomFactor = 1.0;
            offsetX = 0;
            offsetY = 0;

            // Set default values for Mandelbrot set
            redrawFractal(gc, fractalTypeChoice, depthField, colorSchemeChoice,
                    colorPicker, juliaCXField, juliaCYField);
        });

        // Set Julia set preset values
        juliaPreset.setOnAction(e -> {
            juliaCXField.setText("-0.7");
            juliaCYField.setText("0.27015");
            redrawFractal(gc, fractalTypeChoice, depthField, colorSchemeChoice,
                    colorPicker, juliaCXField, juliaCYField);
        });

        // Toggle between light and dark themes
        themeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) applyDarkTheme(layout, inputGrid, statusBar);
            else applyLightTheme(layout, inputGrid, statusBar);
        });

        // Drag to pan the fractal
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                canvas.setCursor(Cursor.CLOSED_HAND);
                startDragX = e.getX();
                startDragY = e.getY();
                prevOffsetX = offsetX;
                prevOffsetY = offsetY;
            }
        });

        // Drag to pan the fractal
        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double deltaX = e.getX() - startDragX;
                double deltaY = e.getY() - startDragY;
                offsetX = prevOffsetX - (deltaX * 3) / (zoomFactor * WIDTH);
                offsetY = prevOffsetY - (deltaY * 2) / (zoomFactor * HEIGHT);
                redrawFractal(gc, fractalTypeChoice, depthField, colorSchemeChoice,
                        colorPicker, juliaCXField, juliaCYField);
            }
        });

        // Reset cursor on mouse release or exit
        canvas.setOnMouseReleased(e -> canvas.setCursor(Cursor.DEFAULT));
        canvas.setOnMouseExited(e -> canvas.setCursor(Cursor.DEFAULT));

        // Zoom in/out using mouse scroll
        canvas.setOnScroll(e -> {
            double mouseX = e.getX();
            double mouseY = e.getY();
            double oldZoom = zoomFactor;

            // Zoom in/out based on scroll direction
            zoomFactor *= e.getDeltaY() > 0 ? 1.1 : 0.9;
            zoomFactor = Math.min(Math.max(zoomFactor, MIN_ZOOM), MAX_ZOOM);

            // Adjust the offset based on the zoom factor
            double fractalX = 1.5 * (mouseX - WIDTH/2) / (0.5 * oldZoom * WIDTH) - offsetX;
            double fractalY = (mouseY - HEIGHT/2) / (0.5 * oldZoom * HEIGHT) - offsetY;
            offsetX = 1.5 * (mouseX - WIDTH/2) / (0.5 * zoomFactor * WIDTH) - fractalX;
            offsetY = (mouseY - HEIGHT/2) / (0.5 * zoomFactor * HEIGHT) - fractalY;

            // Redraw the fractal
            redrawFractal(gc, fractalTypeChoice, depthField, colorSchemeChoice,
                    colorPicker, juliaCXField, juliaCYField);
        });

        // Update status text on mouse move
        canvas.setOnMouseMoved(e -> updateStatus(e.getX(), e.getY()));

        // Add tooltips to input controls and show the stage
        addTooltips(depthField, colorSchemeChoice, backgroundPicker, insideColorPicker,
                colorPicker, juliaCXField, juliaCYField);

        // Set the scene and show the stage
        primaryStage.setTitle("Enhanced Fractal Renderer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set default values for Mandelbrot set
        redrawFractal(gc, fractalTypeChoice, depthField, colorSchemeChoice,
                colorPicker, juliaCXField, juliaCYField);
    }

    // Redraw the fractal based on the selected type
    private void redrawFractal(GraphicsContext gc, ChoiceBox<String> fractalType, TextField depthField,
                               ChoiceBox<String> colorScheme, ColorPicker colorPicker,
                               TextField juliaCXField, TextField juliaCYField) {
        // Draw the fractal based on the selected type
        try {
            // Clear the canvas
            gc.clearRect(0, 0, WIDTH, HEIGHT);
            String type = fractalType.getValue();
            int param = Integer.parseInt(depthField.getText());
            String colorMode = colorScheme.getValue();
            customColor = colorPicker.getValue();

            // Draw the fractal based on the selected type
            switch (type) {
                // Draw the Triangle Pyramid
                case "Sierpinski Triangle":
                    gc.setFill(backgroundPicker.getValue());
                    gc.fillRect(0, 0, WIDTH, HEIGHT);
                    drawSierpinski(gc, param, getFractalColor(colorMode, customColor));
                    break;
                    // Draw the Mandelbrot Set
                case "Mandelbrot Set":
                    fractalImage = drawMandelbrotSet(param, colorMode, insideColorPicker.getValue());
                    gc.drawImage(fractalImage, 0, 0);
                    break;
                    // Draw the Julia Set
                case "Julia Set":
                    double cX = Double.parseDouble(juliaCXField.getText());
                    double cY = Double.parseDouble(juliaCYField.getText());
                    fractalImage = drawJuliaSet(param, cX, cY, colorMode, insideColorPicker.getValue());
                    gc.drawImage(fractalImage, 0, 0);
                    break;
                    // Draw the Koch Snowflake
                case "Koch Snowflake":
                    gc.setFill(backgroundPicker.getValue());
                    gc.fillRect(0, 0, WIDTH, HEIGHT);
                    drawKochSnowflake(gc, param, getFractalColor(colorMode, customColor));
                    break;
            }
        } catch (Exception ex) {
            showAlert("Error", ex.getMessage());
        }
    }

    // Get the fractal color based on the color mode
    private Color getFractalColor(String colorMode, Color customColor) {
        switch (colorMode) {
            case "Monochrome": return Color.BLACK;
            case "Custom": return customColor;
            default: return Color.BLACK;
        }
    }

    // Draw a Sierpinski triangle with a given depth
    private void drawSierpinski(GraphicsContext gc, int depth, Color color) {
        gc.setFill(color);
        drawSierpinskiRecursive(gc, WIDTH/2, 50, WIDTH-50, HEIGHT-50, 50, HEIGHT-50, depth);
    }

    // Draw a Sierpinski triangle with a given depth
    private void drawSierpinskiRecursive(GraphicsContext gc, double x1, double y1, double x2, double y2,
                                         double x3, double y3, int depth) {
        if (depth == 0) {
            gc.fillPolygon(new double[]{x1, x2, x3}, new double[]{y1, y2, y3}, 3);
        } else {
            // Calculate midpoints of the sides
            double midX1 = (x1 + x2) / 2;
            double midY1 = (y1 + y2) / 2;
            double midX2 = (x2 + x3) / 2;
            double midY2 = (y2 + y3) / 2;
            double midX3 = (x3 + x1) / 2;
            double midY3 = (y3 + y1) / 2;

            drawSierpinskiRecursive(gc, x1, y1, midX1, midY1, midX3, midY3, depth-1);
            drawSierpinskiRecursive(gc, midX1, midY1, x2, y2, midX2, midY2, depth-1);
            drawSierpinskiRecursive(gc, midX3, midY3, midX2, midY2, x3, y3, depth-1);
        }
    }

    // Draw a Mandelbrot set with given parameters
    private WritableImage drawMandelbrotSet(int maxIterations, String colorMode, Color insideColor) {
        WritableImage image = new WritableImage(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                double zx = 1.5 * (x - WIDTH/2) / (0.5 * zoomFactor * WIDTH) - offsetX;
                double zy = (y - HEIGHT/2) / (0.5 * zoomFactor * HEIGHT) - offsetY;
                double cX = zx;
                double cY = zy;
                int iter = maxIterations;

                while (zx*zx + zy*zy < 4 && iter > 0) {
                    double tmp = zx*zx - zy*zy + cX;
                    zy = 2*zx*zy + cY;
                    zx = tmp;
                    iter--;
                }
                image.getPixelWriter().setColor(x, y, getColor(iter, maxIterations, colorMode, insideColor));
            }
        }
        return image;
    }

    // Draw a Julia set with given parameters
    private WritableImage drawJuliaSet(int maxIterations, double cX, double cY, String colorMode, Color insideColor) {
        // Exportable image
        WritableImage image = new WritableImage(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                double zx = 1.5 * (x - WIDTH/2) / (0.5 * zoomFactor * WIDTH) - offsetX;
                double zy = (y - HEIGHT/2) / (0.5 * zoomFactor * HEIGHT) - offsetY;
                int iter = maxIterations;

                while (zx*zx + zy*zy < 4 && iter > 0) {
                    double tmp = zx*zx - zy*zy + cX;
                    zy = 2*zx*zy + cY;
                    zx = tmp;
                    iter--;
                }
                image.getPixelWriter().setColor(x, y, getColor(iter, maxIterations, colorMode, insideColor));
            }
        }
        return image;
    }

    // Draw a koch snowflake with given depth
    private void drawKochSnowflake(GraphicsContext gc, int depth, Color color) {
        double length = WIDTH * 0.6;
        double height = length * Math.sqrt(3) / 2;
        double x1 = (WIDTH - length) / 2;
        double y1 = HEIGHT/2 + height/3;
        double x2 = WIDTH/2;
        double y2 = HEIGHT/2 - 2*height/3;
        double x3 = (WIDTH + length)/2;
        double y3 = HEIGHT/2 + height/3;

        // Draw equil. Triangle
        gc.setStroke(color);
        drawKochLine(gc, x1, y1, x2, y2, depth);
        drawKochLine(gc, x2, y2, x3, y3, depth);
        drawKochLine(gc, x3, y3, x1, y1, depth);
    }

    // Draw a Koch line with given depth
    private void drawKochLine(GraphicsContext gc, double x1, double y1, double x5, double y5, int depth) {
        if (depth == 0) {
            gc.strokeLine(x1, y1, x5, y5);
        } else {
            double dx = x5 - x1;
            double dy = y5 - y1;
            double x2 = x1 + dx/3;
            double y2 = y1 + dy/3;
            double x3 = (x1 + x5)/2 + Math.sqrt(3)*(y1 - y5)/6;
            double y3 = (y1 + y5)/2 + Math.sqrt(3)*(x5 - x1)/6;
            double x4 = x1 + 2*dx/3;
            double y4 = y1 + 2*dy/3;

            drawKochLine(gc, x1, y1, x2, y2, depth-1);
            drawKochLine(gc, x2, y2, x3, y3, depth-1);
            drawKochLine(gc, x3, y3, x4, y4, depth-1);
            drawKochLine(gc, x4, y4, x5, y5, depth-1);
        }
    }

    // Get color based on iteration count and color mode
    private Color getColor(int iter, int maxIter, String mode, Color insideColor) {
        if (iter == maxIter) return insideColor;
        switch (mode) {
            case "Rainbow": return Color.hsb(360.0 * iter / maxIter, 1, 1);
            case "Custom": return customColor;
            default: return Color.WHITE;
        }
    }

    // Update status text with zoom factor and fractal coordinates
    private void updateStatus(double x, double y) {
        double fractalX = 1.5 * (x - WIDTH/2) / (0.5 * zoomFactor * WIDTH) - offsetX;
        double fractalY = (y - HEIGHT/2) / (0.5 * zoomFactor * HEIGHT) - offsetY;
        statusText.setText(String.format("Zoom: %.2fx | Coordinates: (%.4f, %.4f)", zoomFactor, fractalX, fractalY));
    }

    // Saves Image to File
    private void saveImage(Canvas canvas, Stage stage) {
        try {
            WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image, null), "png", file);
                showAlert("Success", "Image saved successfully!");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to save image: " + e.getMessage());
        }
    }

    // Show an alert dialog with the given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Reset the view to default settings!
    private void resetView(GraphicsContext gc, ChoiceBox<String> fractalType, TextField depthField,
                           ChoiceBox<String> colorScheme, ColorPicker colorPicker,
                           TextField juliaCXField, TextField juliaCYField) {
        zoomFactor = 1.0;
        offsetX = 0;
        offsetY = 0;
        redrawFractal(gc, fractalType, depthField, colorScheme, colorPicker, juliaCXField, juliaCYField);
    }

    // Applies Dark Theme
    private void applyDarkTheme(Parent... nodes) {
        for (Parent node : nodes) {
            node.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");
        }
    }

    // Applies Light Theme
    private void applyLightTheme(Parent... nodes) {
        for (Parent node : nodes) {
            node.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        }
    }

    // Add tooltips to input controls
    private void addTooltips(Control... controls) {
        for (Control control : controls) {
            if (control instanceof TextField) {
                if (control.getId() == null) {
                    switch (((TextField) control).getPromptText()) {
                        case "Depth/Iterations":
                            control.setTooltip(new Tooltip("Set recursion depth or iteration count"));
                            break;
                        case "Julia cX":
                            control.setTooltip(new Tooltip("Real part of Julia set constant"));
                            break;
                        case "Julia cY":
                            control.setTooltip(new Tooltip("Imaginary part of Julia set constant"));
                            break;
                    }
                }
            } else if (control instanceof ChoiceBox) {
                control.setTooltip(new Tooltip("Select color scheme"));
            } else if (control instanceof ColorPicker) {
                if (control == backgroundPicker) {
                    control.setTooltip(new Tooltip("Choose background color for geometric fractals"));
                } else if (control == insideColorPicker) {
                    control.setTooltip(new Tooltip("Choose inside color for Mandelbrot/Julia sets"));
                }
            }
        }
    }

    // Launch the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}