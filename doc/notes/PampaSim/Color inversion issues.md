Couldn't make text stand out from background color inside table cells, I believe cells don't support rich text (bizarrely)

```java
double intensity = 0.0
  + procClr.getRed()*0.299
  + procClr.getGreen()*0.587
  + procClr.getBlue()*0.114; // > 186;
boolean invertColor = intensity < (186.0/255.0);
if (invertColor) {
  getManagedChildren().forEach(node -> {
    node.setStyle("-fx-text-fill: whitesmoke");
  });
}
```
Got this logic from https://stackoverflow.com/a/3943023