# Enhanced Fractal Renderer

The **Enhanced Fractal Renderer** is a JavaFX application designed to generate and visualize various types of fractals. It supports multiple fractal types, including the Sierpinski Triangle, Mandelbrot Set, Julia Set, and Koch Snowflake. The application provides a user-friendly interface with interactive features such as zooming, panning, and customizable color schemes.

## Features

- **Multiple Fractal Types**: Choose from Sierpinski Triangle, Mandelbrot Set, Julia Set, and Koch Snowflake.
- **Interactive Controls**: Zoom in/out using the mouse scroll and pan by dragging the canvas.
- **Customizable Color Schemes**: Select from Monochrome, Rainbow, or Custom color schemes.
- **Save Images**: Export the rendered fractal as a PNG image.
- **Dark/Light Theme**: Toggle between dark and light themes for better visibility.
- **Preset Values**: Quickly set Mandelbrot and Julia set parameters with preset buttons.
- **Real-time Status**: View zoom level and fractal coordinates in real-time.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher.
- JavaFX SDK (included in JDK 11+ or available separately).

### Running the Application

1. Clone the repository or download the source code.
2. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
3. Ensure that the JavaFX SDK is correctly configured in your IDE.
4. Run the `FractalRenderer` class to launch the application.

### Usage

1. **Select Fractal Type**: Choose the type of fractal you want to render from the dropdown menu.
2. **Set Parameters**: Adjust the depth/iterations, color scheme, and other parameters as needed.
3. **Draw Fractal**: Click the "Draw Fractal" button to render the fractal on the canvas.
4. **Zoom and Pan**: Use the mouse scroll to zoom in/out and drag to pan across the fractal.
5. **Save Image**: Click the "Save Image" button to export the fractal as a PNG file.
6. **Toggle Theme**: Switch between dark and light themes using the "Dark Mode" toggle button.

## Fractal Types

### Sierpinski Triangle

A classic example of a self-similar fractal. The depth parameter controls the number of recursive subdivisions.

### Mandelbrot Set

A famous fractal that reveals intricate details at different zoom levels. The iteration count determines the level of detail.

### Julia Set

Similar to the Mandelbrot Set but defined by a different complex constant. The `cX` and `cY` parameters control the shape of the fractal.

### Koch Snowflake

A fractal curve that starts with an equilateral triangle and recursively adds smaller triangles to each side.

## Customization

- **Color Schemes**: Choose from Monochrome, Rainbow, or Custom color schemes.
- **Background Color**: Set the background color for geometric fractals (Sierpinski Triangle and Koch Snowflake).
- **Inside Color**: Set the color for the interior of Mandelbrot and Julia sets.


## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by various fractal visualization projects and mathematical literature.
- Built using JavaFX for the graphical user interface.

---

Enjoy exploring the fascinating world of fractals with the Enhanced Fractal Renderer!
