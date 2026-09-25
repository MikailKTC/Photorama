# Photorama

**Photorama** is a Java-based image gallery application designed to detect and group visually similar or duplicate images.

The application provides two interfaces:

* 🖥️ **JavaFX graphical interface** for browsing image galleries and exploring detected duplicates
* 💻 **Command-line interface** for testing image comparison algorithms and generating duplicate detection reports

The project focuses on image processing, similarity detection, algorithm design, object-oriented programming, and graphical user interface development.

---

## 📌 Overview

Photorama analyzes a collection of images and identifies images that are sufficiently similar to be considered duplicates.

The application is designed to detect more than exact file duplicates. Images can still be considered similar when they have undergone modifications such as:

* Pixelation
* Blurring
* Color alterations
* Resizing
* Watermarks
* Small changes in camera position
* Other minor visual modifications

The images are converted to grayscale and represented as pixel matrices before being analyzed by the comparison algorithms.

---

## 🎯 Main Features

### Image Gallery

* Open a directory containing images
* Automatically detect `.png` and `.jpg` files
* Organize images into groups of similar images
* Display one representative image from each group
* Explore all duplicates associated with a selected image

### Duplicate Detection

Photorama implements three different image similarity algorithms:

1. **Pixel Comparison**
2. **Average Hashing**
3. **Difference Hashing**

Each algorithm can be configured with different tolerance parameters to make the comparison more strict or more flexible.

### Graphical Interface

The JavaFX interface allows users to:

* Select an image directory
* Choose a duplicate detection algorithm
* Choose the desired tolerance level
* Browse detected image groups
* Select an image to view it in a larger format
* View the duplicates associated with that image
* Exit the application using `Escape`

### Command-Line Interface

The command-line version provides debugging and testing information, including:

* Pixel comparison results
* Generated 8×8 image hashes
* Detected duplicate groups
* Results using different algorithm parameters

---

# 🧠 Image Similarity Algorithms

## 1. Pixel Comparison

The first algorithm compares two images pixel by pixel.

For each corresponding pixel, the absolute difference between their grayscale values is calculated.

A pixel is considered different when:

```text
|pixel1 - pixel2| > threshold
```

The images are considered similar when the percentage of different pixels remains below a configurable maximum.

For example:

```text
Difference threshold: 15
Maximum different pixels: 20%
```

This approach works particularly well when the images have the same dimensions and only relatively small pixel-level modifications.

If two images have different dimensions, they are considered different by this algorithm.

---

## 2. Average Hashing

The second algorithm generates a compact representation of an image based on its average brightness.

### Process

1. Resize the image to `8 × 8`
2. Calculate the average pixel value
3. Compare every pixel against the average
4. Generate an `8 × 8` binary hash

Each position in the hash contains:

* `0` if the pixel is darker than or equal to the average
* `1` if the pixel is brighter than the average

Example:

```text
00001111
00001111
00001111
00001111
10001111
10001111
10001111
10001111
```

Two images can then be compared by counting the number of different positions in their hashes.

This allows the application to compare compact representations rather than every pixel of the original images.

---

## 3. Difference Hashing

The third algorithm also generates an `8 × 8` binary hash, but instead of comparing pixels to the image average, it compares each pixel with the pixel directly below it.

### Process

1. Resize the image to `8 × 9`
2. Compare each pixel with its neighboring pixel below
3. Generate an `8 × 8` binary hash
4. Compare the resulting hashes

For each position:

```text
0 → current pixel ≤ pixel below
1 → current pixel > pixel below
```

As with average hashing, two images are considered similar when the number of different positions between their hashes remains below a configurable threshold.

---

# 🗂️ Image Grouping

After determining whether images are similar, Photorama groups them together.

The grouping process:

1. Sort the image filenames
2. Select the first remaining image
3. Find all images similar to it
4. Create a duplicate group
5. Remove those images from the remaining collection
6. Repeat until every image has been assigned to a group

For example:

```text
Input:

a.png
b.png
c.png
d.png
e.png
f.png
```

Possible result:

```text
Group 1 → a.png, b.png, e.png
Group 2 → c.png
Group 3 → d.png, f.png
```

Images without duplicates remain in their own group.

---

# 🖥️ JavaFX Interface

The graphical interface is designed around an image gallery workflow.

### Initial State

When the application starts, no gallery is loaded and a temporary image is displayed.

### Selecting a Gallery

The user can select a directory containing images using a directory selection dialog.

The application then:

1. Loads the images
2. Applies the selected comparison algorithm
3. Groups similar images
4. Displays the resulting gallery

### Gallery View

The main gallery displays one representative image from each duplicate group.

For example, if the detected groups are:

```text
[a.png, b.png, e.png]
[c.png]
[d.png, f.png]
```

The main gallery displays:

```text
a.png    c.png    d.png
```

### Exploring Duplicates

When the user selects an image:

1. The selected image is displayed in a larger view
2. The bottom section displays the other images belonging to the same duplicate group
3. Selecting one of these images updates the main preview

This allows users to quickly inspect why images were classified as duplicates.

---

# ⚙️ Tolerance Settings

The JavaFX interface provides two levels of comparison tolerance:

| Algorithm          |                      Low Tolerance |                     High Tolerance |
| ------------------ | ---------------------------------: | ---------------------------------: |
| Pixel Comparison   | Difference threshold: 20 / Max 10% | Difference threshold: 30 / Max 40% |
| Average Hashing    |             Max 10 different cells |             Max 15 different cells |
| Difference Hashing |             Max 10 different cells |             Max 15 different cells |

A lower tolerance produces stricter comparisons, while a higher tolerance allows more variation between images.

---

# 🏗️ Architecture

The project separates the application's image-processing logic from its user interfaces.

The main components include:

```text
                    ┌──────────────────┐
                    │     Gallerie     │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │ ComparateurImages│
                    └────────┬─────────┘
                             │
             ┌───────────────┼────────────────┐
             ▼               ▼                ▼
      ┌─────────────┐ ┌──────────────┐ ┌────────────────┐
      │    Pixel    │ │ Average Hash │ │ Difference Hash│
      └─────────────┘ └──────────────┘ └────────────────┘

             ▲                              ▲
             │                              │
     ┌───────┴───────┐              ┌──────┴───────┐
     │   MainCmd     │              │  MainJavaFX  │
     │  CLI Interface│              │  GUI Interface│
     └───────────────┘              └───────────────┘
```

The `Gallerie` class is responsible for loading and grouping images, while the `ComparateurImages` hierarchy provides interchangeable similarity algorithms.

This design allows the gallery logic to work independently of the interface being used.

---

# 🧩 Core Components

### `Gallerie`

Responsible for:

* Loading image files from a directory
* Identifying supported image formats
* Grouping similar images
* Maintaining the duplicate groups

### `ComparateurImages`

Defines the common behavior used to determine whether two images are similar.

The project includes three implementations:

* `ComparateurImagesPixels`
* `ComparateurImagesHachageMoyenne`
* `ComparateurImagesHachageDifference`

### `MainJavaFX`

Responsible for:

* Starting the JavaFX application
* Managing graphical components
* Handling user interactions
* Displaying errors to the user

### `MainCmd`

Responsible for:

* Running the command-line version
* Displaying algorithm results
* Printing debugging information
* Reporting errors when invalid images are encountered

---

# 🛡️ Error Handling

The application is designed to handle invalid image files without crashing.

A dedicated corrupted-image dataset is used to test file-handling behavior.

When an invalid image is encountered:

### Command Line

The application displays an appropriate error message and terminates cleanly.

### JavaFX

The application displays an error message in the graphical interface instead of crashing.

This keeps file-processing errors separate from the application's core image-processing logic.

---

# 📁 Project Structure

```text
Photorama/
│
├── airbnb-mini/
├── airbnb-petit/
├── airbnb-large/
├── airbnb-corrompu/
├── debogage/
│
├── src/
│   └── main/
│       └── java/
│           └── ca/
│               └── qc/
│                   └── bdeb/
│                       └── sim/
│                           └── tp1photorama/
│
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

The repository also includes image datasets used to test different aspects of the duplicate detection system.

---

# 🛠️ Technologies

* **Java 21**
* **JavaFX 21**
* **Gradle**
* **IntelliJ IDEA**
* **Object-Oriented Programming**
* **Image Processing**
* **Java Collections**
* **File I/O**
* **Exception Handling**

The project is configured with Gradle and uses JavaFX modules for the graphical interface.

---

# 🚀 Running the Application

The project uses Gradle to provide separate entry points for the graphical and command-line versions.

### JavaFX Version

Run the standard application task:

```bash
./gradlew run
```

On Windows:

```bash
gradlew.bat run
```

### Command-Line Version

Run:

```bash
./gradlew runCmd
```

On Windows:

```bash
gradlew.bat runCmd
```

The Gradle configuration defines `MainJavaFX` as the graphical application's main class and `MainCmd` for the command-line version.

---

# 📊 Test Datasets

The project includes several image collections for testing:

### `airbnb-mini`

A small dataset containing 9 images, useful for quick tests.

### `airbnb-petit`

A dataset containing 70 images in smaller formats.

### `airbnb-large`

A larger-format version of the same 70-image dataset, useful for testing algorithm performance.

### `airbnb-corrompu`

A dataset containing invalid images used to test exception handling.

### `debogage`

A collection of simple test images used to verify the behavior of the different similarity algorithms.

---

# 🔬 Concepts Demonstrated

This project combines several software engineering and computer science concepts:

* Object-oriented design
* Class hierarchies
* Polymorphism
* Encapsulation
* ArrayLists and nested collections
* File system manipulation
* Image representation using pixel matrices
* Image hashing
* Similarity detection
* Algorithm comparison
* Parameterized algorithms
* Exception handling
* JavaFX event handling
* GUI development
* Separation between application logic and presentation

---

# 📈 Performance

Image comparison can become computationally expensive as the number and size of images increase.

The pixel-based algorithm directly compares image pixels, while the hashing algorithms first reduce images to compact `8 × 8` representations.

For larger datasets, avoiding repeated hash calculations can significantly improve performance.

The project is therefore also an exploration of the trade-off between comparison accuracy, flexibility, and computational cost.

---

# 📸 Screenshots

Add screenshots of the application here:

```markdown
## Screenshots

### Main Interface

![Photorama Interface](images/application.png)

### Duplicate Detection

![Duplicate Detection](images/duplicates.png)
```

---

# 🔗 Repository

**GitHub:**
https://github.com/MikailKTC/Photorama

---

## Project Summary

Photorama combines **Java, JavaFX, image processing, and algorithm design** to create an interactive image gallery capable of detecting and organizing visually similar photographs.

The project demonstrates how different similarity algorithms can be implemented behind a common interface and used by both a graphical application and a command-line testing environment.
