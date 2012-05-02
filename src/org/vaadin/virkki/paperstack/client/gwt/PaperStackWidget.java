package org.vaadin.virkki.paperstack.client.gwt;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * PaperStack widget.
 * 
 * @author Tomi Virkki / Vaadin Ltd
 */
public final class PaperStackWidget extends Composite {
    private final SimplePanel previousContainer = new SimplePanel();
    private final CurrentPanel currentContainer = new CurrentPanel();
    private final SimplePanel nextContainer = new SimplePanel();
    private final PaperStackCanvas canvas = new PaperStackCanvas();
    private final Image backButton = new Image(GWT.getModuleBaseURL() + "paperstack/backarrow.png");
    private final PaperStackAnimation animation = new PaperStackAnimation();
    private PaperStackWidgetListener listener;

    public PaperStackWidget() {
        final FlowPanel rootPanel = new FlowPanel();
        rootPanel.setStyleName("v-paperstack");
        initWidget(rootPanel);

        rootPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        rootPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

        nextContainer.setSize("100%", "100%");
        rootPanel.add(nextContainer);

        previousContainer.setSize("100%", "100%");
        rootPanel.add(previousContainer);

        rootPanel.add(currentContainer);
        rootPanel.add(canvas);

        final Style backButtonStyle = backButton.getElement().getStyle();
        backButtonStyle.setPosition(Position.ABSOLUTE);
        backButtonStyle.setBottom(0.0, Unit.PX);
        backButtonStyle.setCursor(Cursor.POINTER);
        backButton.setVisible(false);
        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                backButton.setVisible(false);
                navigate(false);
            }
        });
        rootPanel.add(backButton);
    }

    public void setListener(final PaperStackWidgetListener listener) {
        this.listener = listener;
    }

    public void setPreviousWidget(final Widget widget, final String backgroundColor) {
        previousContainer.setWidget(widget);
        previousContainer.getElement().getStyle().setBackgroundColor(backgroundColor);
        backButton.setVisible(widget != null);
    }

    public void setCurrentWidget(final Widget widget, final String backgroundColor) {
        currentContainer.setWidget(widget, backgroundColor);
    }

    public void setNextWidget(final Widget widget, final String backgroundColor) {
        nextContainer.setWidget(widget);
        nextContainer.getElement().getStyle().setBackgroundColor(backgroundColor);
        if (widget != null) {
            canvas.switchSize(false);
            if (canvas.isVisible()) {
                canvas.render(canvas.initialCornerX, canvas.initialCornerY);
            } else {
                canvas.setVisible(true);
                animation.animate(canvas.initialCanvasWidth - 1, 1, PaperStackAnimation.CURRENT_COMPONENT);
            }
        }
    }

    public void setCloneCount(final int cloneCount) {
        currentContainer.cloneStyles = new Style[cloneCount];
    }

    public void setPaperColors(final String back, final String edge) {
        canvas.paperBackColor = CssColor.make(back);
        canvas.paperEdgeColor = CssColor.make(edge);
    }

    public void setFoldCoordinates(final int initialCanvasWidth, final int initialCanvasHeight,
            final int initialCornerX, final int initialCornerY) {
        canvas.initialCanvasWidth = initialCanvasWidth;
        canvas.initialCanvasHeight = initialCanvasHeight;
        canvas.initialCornerX = initialCornerX;
        canvas.initialCornerY = initialCornerY;
    }

    public void navigate(final boolean forward) {
        if (!animation.running) {
            canvas.setFocus(true);
            if (forward && (nextContainer.getWidget() != null)) {
                canvas.switchSize(true);
                currentContainer.generateClones();
                animation.animate(getOffsetWidth() - (canvas.initialCanvasWidth - canvas.initialCornerX),
                        canvas.initialCornerY, PaperStackAnimation.NEXT_COMPONENT);
            } else if (!forward && (previousContainer.getWidget() != null)) {
                nextContainer.getElement().getStyle().setBackgroundColor(currentContainer.backgroundColor);
                nextContainer.setWidget(currentContainer.widget);

                currentContainer.setWidget(previousContainer.getWidget(), previousContainer.getElement()
                        .getStyle().getBackgroundColor());
                currentContainer.generateClones();

                canvas.switchSize(true);
                canvas.setVisible(true);

                animation.animate(-getOffsetWidth(), 2 * getOffsetHeight(),
                        PaperStackAnimation.PREVIOUS_COMPONENT);
            }
        }
    }

    private class CurrentPanel extends FlowPanel {
        private Widget widget;
        private final Element cloneElementModel = DOM.createDiv();
        private Style[] cloneStyles = new Style[10];
        private String backgroundColor;

        public CurrentPanel() {
            final Style cloneElementStyle = cloneElementModel.getStyle();
            cloneElementStyle.setBottom(0.0, Unit.PX);
            cloneElementStyle.setPosition(Position.ABSOLUTE);
            cloneElementStyle.setOverflow(Overflow.HIDDEN);
            cloneElementStyle.setWidth(100.0, Unit.PCT);
            cloneElementStyle.setHeight(100.0, Unit.PCT);

            addStyleName("currentpanel");
            setSize("100%", "100%");
            getElement().getStyle().setBottom(0.0, Unit.PX);
            getElement().getStyle().setPosition(Position.ABSOLUTE);
        }

        public void setWidget(final Widget widget, final String backgroundColor) {
            this.widget = widget;
            this.backgroundColor = backgroundColor;

            cloneElementModel.getStyle().setBackgroundColor(backgroundColor);
            setVisible(true);
            restore();
        }

        public void generateClones() {
            clear();
            getElement().getStyle().clearBackgroundColor();

            Element frontMost = null;
            for (int i = 0; i < cloneStyles.length; i++) {
                Node node = widget.getElement();
                if (i != (cloneStyles.length / 2)) {
                    node = node.cloneNode(true);
                }

                final Element widgetWrapper = (Element) cloneElementModel.cloneNode(false);
                final Style wrapperStyle = widgetWrapper.getStyle();
                wrapperStyle.setWidth(getOffsetWidth(), Unit.PX);
                wrapperStyle.setHeight(getOffsetHeight(), Unit.PX);
                widgetWrapper.appendChild(node);

                final Element clone = (Element) cloneElementModel.cloneNode(false);
                clone.appendChild(widgetWrapper);
                cloneStyles[i] = clone.getStyle();
                if (i == (cloneStyles.length / 2)) {
                    frontMost = clone;
                } else {
                    getElement().appendChild(clone);
                }
            }
            if (frontMost != null) {
                getElement().appendChild(frontMost);
            }
        }

        public void restore() {
            clear();
            add(widget);
            getElement().getStyle().setBackgroundColor(backgroundColor);
        }
    }

    /**
     * The canvas layer which handles the transition effect between
     * subcomponents.
     */
    private class PaperStackCanvas extends FocusWidget {
        private final Context2d context;

        private double canvasWidth;
        private double canvasHeight;
        private double initialCornerX = 5.0;
        private double initialCornerY = 10.0;
        private double initialCanvasWidth = 35.0;
        private double initialCanvasHeight = 55.0;

        private final CssColor shadowColor = CssColor.make("rgba(0, 0, 0, 0.3)");
        public CssColor paperBackColor = CssColor.make("#00B4F0");
        public CssColor paperEdgeColor = CssColor.make("#000");

        private Duration dragStart;
        private boolean fullSize;

        public PaperStackCanvas() {
            final Canvas canvas = Canvas.createIfSupported();
            context = canvas.getContext2d();
            setElement(canvas.getElement());
            setVisible(false);

            final Style style = getElement().getStyle();
            style.setRight(0.0, Unit.PX);
            style.setTop(0.0, Unit.PX);
            style.setPosition(Position.ABSOLUTE);

            addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(final MouseDownEvent event) {
                    event.preventDefault();
                    grab(event.getRelativeX(PaperStackWidget.this.getElement()),
                            event.getRelativeY(PaperStackWidget.this.getElement()));
                }
            });
            addMouseMoveHandler(new MouseMoveHandler() {
                @Override
                public void onMouseMove(final MouseMoveEvent event) {
                    event.preventDefault();
                    if (dragStart != null) {
                        render(event.getX(), event.getY());
                    }
                }
            });
            addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(final MouseUpEvent event) {
                    event.preventDefault();
                    release(event.getX(), event.getY());
                }
            });
            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(final MouseOutEvent event) {
                    event.preventDefault();
                    release(event.getX(), event.getY());
                }
            });
            addTouchStartHandler(new TouchStartHandler() {
                @Override
                public void onTouchStart(final TouchStartEvent event) {
                    event.preventDefault();
                    final Touch touch = event.getTouches().get(0);
                    grab(touch.getRelativeX(PaperStackWidget.this.getElement()),
                            touch.getRelativeY(PaperStackWidget.this.getElement()));
                }
            });
            addTouchMoveHandler(new TouchMoveHandler() {
                @Override
                public void onTouchMove(final TouchMoveEvent event) {
                    event.preventDefault();
                    if (dragStart != null) {
                        final int[] checked = getCheckedXY(event.getTouches().get(0));
                        render(checked[0], checked[1]);
                    }
                }
            });
            addTouchEndHandler(new TouchEndHandler() {
                @Override
                public void onTouchEnd(final TouchEndEvent event) {
                    event.preventDefault();
                    final int[] checked = getCheckedXY(event.getChangedTouches().get(0));
                    release(checked[0], checked[1]);
                }
            });
        }

        private int[] getCheckedXY(final Touch touch) {
            final int x = touch.getPageX() - getAbsoluteLeft();
            final int y = touch.getPageY() - getAbsoluteTop();
            final int checkedX = x < 0 ? 0 : (x > canvasWidth ? (int) canvasWidth : x);
            final int checkedY = y < 0 ? 0 : (y > canvasHeight ? (int) canvasHeight : y);
            return new int[] { checkedX, checkedY };
        }

        private void grab(final int clientX, final int clientY) {
            if ((dragStart == null) && !animation.running) {
                setFocus(true);
                dragStart = new Duration();

                switchSize(true);
                currentContainer.generateClones();

                backButton.setVisible(false);

                nextContainer.setVisible(true);
                render(clientX, clientY);
            }
        }

        private void release(final int clientX, final int clientY) {
            if (dragStart != null) {
                final double y = Math.min(Math.max(clientY, 1.0), canvasHeight);
                final double x = Math.min(Math.max(clientX, 0.0), canvasWidth - 1.0);

                if ((dragStart.elapsedMillis() < 500)
                        || (y > (PaperStackWidget.this.getOffsetHeight() / 1.5))
                        || (x < (PaperStackWidget.this.getOffsetWidth() / 3.0))) {
                    animation.animate(x, y, PaperStackAnimation.NEXT_COMPONENT);
                } else {
                    animation.animate(x, y, PaperStackAnimation.CURRENT_COMPONENT);
                }
                dragStart = null;

                getElement().getStyle().setCursor(Cursor.DEFAULT);
            }
        }

        /**
         * Changes the size of the canvas to either full-sized (the size of the
         * whole component) or initial-sized (minimized to the right upper
         * corner of the component).
         * 
         * @param fullSizeCanvas
         *            should the canvas be maximized or not.
         */
        public final void switchSize(final boolean fullSizeCanvas) {
            fullSize = fullSizeCanvas;
            canvasWidth = fullSize ? PaperStackWidget.this.getOffsetWidth() : initialCanvasWidth;
            canvasHeight = fullSize ? PaperStackWidget.this.getOffsetHeight() : initialCanvasHeight;

            setPixelSize((int) canvasWidth, (int) canvasHeight);
            context.getCanvas().setWidth((int) canvasWidth);
            context.getCanvas().setHeight((int) canvasHeight);
            context.setStrokeStyle(paperEdgeColor);

            if (!fullSize) {
                getElement().getStyle().setCursor(Cursor.POINTER);
            }
        }

        /**
         * Draws the paper corner and a shadow to the canvas. Resizes the clone
         * elements to create an illusion of diagonal element borderline. The
         * resulting sawtooth pattern is patched using the background color of
         * the component.
         * 
         * @param cornerX
         *            x-position of the corner.
         * @param cornerY
         *            y-position of the corner.
         */
        public final void render(final double cornerX, final double cornerY) {
            final double centerX = (cornerX + canvasWidth) / 2.0;
            final double centerY = cornerY / 2.0;

            final double cornerRight = canvasWidth - cornerX;

            final double orthogonalTan = -cornerRight / cornerY;

            final double rightY = centerY - (orthogonalTan * (canvasWidth - centerX));
            final double leftX = centerX + (centerY / orthogonalTan);

            double rightXalt = canvasWidth - ((rightY * cornerRight) / 4000.0);
            double candidate = leftX + 1.0;
            rightXalt = rightXalt > candidate ? rightXalt : candidate;

            double leftYalt = ((canvasWidth - leftX) * cornerY) / 4000.0;
            candidate = rightY - 1.0;
            leftYalt = leftYalt < candidate ? leftYalt : candidate;

            final double controlPointLeftX = leftX + (leftYalt / orthogonalTan);
            final double controlPointRightY = rightY - ((canvasWidth - rightXalt) * orthogonalTan);

            final double curveLeftX = (2 * controlPointLeftX) - leftX;
            final double curveRightY = (2 * controlPointRightY) - rightY;

            final double leftMiddleX = (leftX + cornerX) / 2.0;
            final double leftMiddleY = (leftYalt + cornerY) / 2.0;

            final double leftCurveMiddleX = (leftX + controlPointLeftX) / 2.0;
            final double leftCurveMiddleY = leftYalt / 2.0;

            final double rightMiddleX = (rightXalt + cornerX) / 2.0;
            final double rightMiddleY = (rightY + cornerY) / 2.0;

            final double rightCurveMiddleX = (canvasWidth + rightXalt) / 2.0;
            final double rightCurveMiddleY = (controlPointRightY + rightY) / 2.0;

            final double shadowOffset = (cornerY + cornerRight) / 8.0;

            final double shadowControlPointRightX = (2.5 * rightXalt) - (1.5 * canvasWidth);
            final double shadowControlPointRightY = (0.2 * rightY) + (0.8 * curveRightY);

            final double shadowControlPointLeftX = (0.2 * leftX) + (0.8 * curveLeftX);
            final double shadowControlPointLeftY = leftYalt * 2.5;

            // Clear the canvas.
            context.clearRect(0, 0, canvasWidth, canvasHeight);

            if (fullSize) {
                // Resize the clone elements and patch the resulting sawtooth
                // pattern.
                context.beginPath();
                context.moveTo(canvasWidth, curveRightY);
                context.quadraticCurveTo(canvasWidth, controlPointRightY, rightCurveMiddleX,
                        rightCurveMiddleY);
                context.lineTo(leftCurveMiddleX, leftCurveMiddleY);
                context.quadraticCurveTo(controlPointLeftX, 0.0, curveLeftX - 1.0, 0.0);

                double previousX = curveLeftX > 0.0 ? curveLeftX : 0.0;
                currentContainer.cloneStyles[0].setWidth(previousX--, Unit.PX);

                // Calculate temporary variables for optimal performance.
                final double temp1 = canvasHeight - leftCurveMiddleY;
                final double temp2 = rightCurveMiddleY - leftCurveMiddleY;
                final double temp3 = canvasHeight + 1.0;
                final double temp4 = rightCurveMiddleX - leftCurveMiddleX;

                for (int i = 1; i < (currentContainer.cloneStyles.length - 1); i++) {
                    final double denominator2 = (i - 1.0) / (currentContainer.cloneStyles.length - 3.0);
                    final double newHeight = temp1 - (temp2 * denominator2);
                    currentContainer.cloneStyles[i].setHeight((newHeight > 0.0 ? newHeight : 0.0), Unit.PX);

                    final double y = temp3 - newHeight;
                    context.lineTo(previousX, y);

                    final double newWidth = leftCurveMiddleX + (temp4 * denominator2);
                    previousX = newWidth > 0.0 ? newWidth : 0.0;
                    currentContainer.cloneStyles[i].setWidth(previousX--, Unit.PX);
                    context.lineTo(previousX, y);
                }

                context.lineTo(previousX, curveRightY + 1.0);
                currentContainer.cloneStyles[currentContainer.cloneStyles.length - 1].setHeight(
                        (curveRightY < canvasHeight ? canvasHeight - curveRightY : 0.0), Unit.PX);
                context.closePath();

                context.setFillStyle(currentContainer.backgroundColor);
                context.fill();
            } else if (nextContainer.getWidget() != null) {
                // Fill the upper right corner with the background color of the
                // next subcomponent.
                context.beginPath();
                context.moveTo(canvasWidth, curveRightY);
                context.lineTo(canvasWidth, 0.0);
                context.lineTo(curveLeftX, 0.0);
                context.closePath();
                context.setFillStyle(nextContainer.getElement().getStyle().getBackgroundColor());
                context.fill();
            }

            // Draw the shadow
            context.beginPath();
            context.moveTo(curveLeftX, 0.0);
            context.quadraticCurveTo(controlPointLeftX, 0.0, leftCurveMiddleX, leftCurveMiddleY);
            context.lineTo(rightCurveMiddleX, rightCurveMiddleY);
            context.quadraticCurveTo(canvasWidth, controlPointRightY, canvasWidth, curveRightY);
            context.quadraticCurveTo(shadowControlPointRightX, shadowControlPointRightY, cornerX
                    - shadowOffset, cornerY + shadowOffset);
            context.quadraticCurveTo(shadowControlPointLeftX, shadowControlPointLeftY, curveLeftX, 0.0);
            context.closePath();
            context.setFillStyle(shadowColor);
            context.fill();

            // Draw the paper.
            context.beginPath();
            context.moveTo(curveLeftX, 0.0);
            context.quadraticCurveTo(controlPointLeftX, 0.0, leftCurveMiddleX, leftCurveMiddleY);
            context.lineTo(rightCurveMiddleX, rightCurveMiddleY);
            context.quadraticCurveTo(canvasWidth, controlPointRightY, canvasWidth, curveRightY);
            context.quadraticCurveTo(canvasWidth, controlPointRightY, rightCurveMiddleX, rightCurveMiddleY);
            context.quadraticCurveTo(rightXalt, rightY, rightMiddleX, rightMiddleY);
            context.lineTo(cornerX, cornerY);
            context.lineTo(leftMiddleX, leftMiddleY);
            context.quadraticCurveTo(leftX, leftYalt, leftCurveMiddleX, leftCurveMiddleY);
            context.quadraticCurveTo(controlPointLeftX, 0.0, curveLeftX, 0.0);
            context.setFillStyle(paperBackColor);
            context.setStrokeStyle(paperEdgeColor);
            context.fill();
            context.stroke();
        }
    }

    /**
     * Animation class for the canvas.
     */
    private class PaperStackAnimation extends Animation {
        private static final int ANIMATION_RUNTIME = 500;

        public static final int PREVIOUS_COMPONENT = -1;
        public static final int CURRENT_COMPONENT = 0;
        public static final int NEXT_COMPONENT = 1;
        private int showComponent;

        private boolean running;

        private double startX;
        private double startY;
        private double xd;
        private double yd;

        @Override
        protected void onComplete() {
            if (showComponent == CURRENT_COMPONENT) {
                currentContainer.restore();
                backButton.setVisible(previousContainer.getWidget() != null);
            } else if (listener != null) {
                listener.pageChanged(showComponent == NEXT_COMPONENT);
            }

            if (showComponent == NEXT_COMPONENT) {
                canvas.setVisible(false);
                currentContainer.setVisible(false);
            } else {
                if (showComponent == PREVIOUS_COMPONENT) {
                    currentContainer.restore();
                }
                canvas.switchSize(false);
                canvas.render(canvas.initialCornerX, canvas.initialCornerY);
            }

            canvas.setFocus(false);
            running = false;
        }

        public void animate(final double animStartX, final double animStartY, final int showComponent) {
            startX = animStartX;
            startY = animStartY;
            final double canvasHeight = canvas.getOffsetHeight();
            final double canvasWidth = canvas.getOffsetWidth();

            this.showComponent = showComponent;
            if (showComponent == NEXT_COMPONENT) {
                // Values needed to animate the corner to the same direction
                // where it was released.
                final double componentDiagonalLength = Math.sqrt(Math.pow(canvasHeight, 2.0)
                        + Math.pow(canvasWidth, 2.0));
                final double componentDiagonalAngle = Math.atan(canvasHeight / canvasWidth);
                final double alpha = Math.atan(animStartY / (canvasWidth - animStartX));
                final double r = Math.cos(alpha - componentDiagonalAngle) * componentDiagonalLength;

                xd = canvasWidth - animStartX - (2.0 * r * Math.cos(alpha));
                yd = (2.0 * r * Math.sin(alpha)) - animStartY;
            } else {
                xd = ((canvasWidth - canvas.initialCanvasWidth) + canvas.initialCornerX) - animStartX;
                yd = canvas.initialCornerY - animStartY;
            }

            running = true;
            run(ANIMATION_RUNTIME);
        }

        @Override
        protected void onUpdate(final double progress) {
            canvas.render(startX + (xd * progress), startY + (yd * progress));
        }
    }
}
