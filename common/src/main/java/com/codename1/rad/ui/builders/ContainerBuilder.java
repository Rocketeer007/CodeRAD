/*
 * The MIT License
 *
 * Copyright 2021 shannah.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.*;

import java.util.Map;

/**
 *
 * @author shannah
 */
@RAD(tag={"container", "border", "x", "y", "flow", "layered", "grid"})
public class ContainerBuilder extends AbstractComponentBuilder<Container> {
    
    
    
    private String uiid = "Container";
    public ContainerBuilder(@Inject ViewContext context, String tagName, Map<String,String> attributes) {
        super(context, tagName, attributes);
        
    }
    
    public ContainerBuilder layout(String layout) {
        String lcLayout = layout.toLowerCase();
        if ("border".equals(lcLayout)) {
            return layout(new BorderLayout());
        } else if ("y".equals(lcLayout)) {
            return layout(BoxLayout.y());
        } else if ("x".equals(lcLayout)) {
            return layout(BoxLayout.x());
        } else if ("flow".equals(lcLayout)) {
            return layout(new FlowLayout());
        } else {
            throw new IllegalArgumentException("Layout "+layout+" not supported by ContainerBuilder");
        }
        
    }
    
    public ContainerBuilder layout(Layout layout) {
        getComponent().setLayout(layout);
        return this;
    }
    
    
    @Override
    public Container build() {
        String tagName = getTagName();
        Layout layout = null;
        if (tagName != null) {
            if ("border".equals(tagName)) {
                layout = new BorderLayout();
            } else if ("y".equals(tagName)) {
                layout = BoxLayout.y();
            } else if ("x".equals(tagName)) {
                layout = BoxLayout.x();
            } else if ("flow".equals(tagName)) {
                layout = new FlowLayout();
            } else if ("grid".equals(tagName)) {
                int columns = 2;
                if (hasAttribute("layout-columns")) {
                    columns = Integer.parseInt(getAttribute("layout-columns"));
                }
                if (hasAttribute("layout-rows")) {
                    int rows = Integer.parseInt(getAttribute("layout-rows"));
                    layout = new GridLayout(rows, columns);
                } else {
                    layout = new GridLayout(columns);
                }
            } else if ("layered".equals(tagName)) {
                layout = new LayeredLayout();
            }
        }
        if (layout != null) {
            return new Container(layout);
        } else {
            return new Container();
        }
    }

    @Override
    public Object parseConstraint(String constraint) {
        if ("north".equals(constraint)) return BorderLayout.NORTH;
        if ("south".equals(constraint)) return BorderLayout.SOUTH;
        if ("east".equals(constraint)) return BorderLayout.EAST;
        if ("west".equals(constraint)) return BorderLayout.WEST;
        if ("center".equals(constraint)) return BorderLayout.CENTER;
        return null;
    }

    
}