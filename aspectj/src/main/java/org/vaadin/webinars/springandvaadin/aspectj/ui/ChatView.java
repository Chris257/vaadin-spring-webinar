/*
 * Copyright 2014 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.webinars.springandvaadin.aspectj.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.vaadin.webinars.springandvaadin.aspectj.backend.ChatMessage;
import org.vaadin.webinars.springandvaadin.aspectj.backend.ChatService;
import org.vaadin.webinars.springandvaadin.aspectj.backend.MessagePostedEvent;

import javax.annotation.PostConstruct;

/**
 * @author petter@vaadin.com
 */
@Configurable
public class ChatView extends VerticalLayout implements View, ApplicationListener<MessagePostedEvent> {

    @Autowired
    transient ChatService chatService;
    @Autowired
    transient ApplicationEventMulticaster eventMulticaster;
    private Label roomLabel;
    private Panel messagesPanel;
    private VerticalLayout messagesLayout;
    private TextField message;
    private Button post;
    private String room;

    @PostConstruct
    void init() {
        setSizeFull();
        setMargin(true);
        setSpacing(true);

        roomLabel = new Label();
        roomLabel.addStyleName(Reindeer.LABEL_H1);
        addComponent(roomLabel);

        messagesPanel = new Panel();
        messagesPanel.setSizeFull();
        addComponent(messagesPanel);
        setExpandRatio(messagesPanel, 1f);

        messagesLayout = new VerticalLayout();
        messagesLayout.setMargin(true);
        messagesLayout.setSpacing(true);
        messagesPanel.setContent(messagesLayout);

        HorizontalLayout bar = new HorizontalLayout();
        bar.setSpacing(true);
        addComponent(bar);

        message = new TextField();
        bar.addComponent(message);

        post = new Button("Post", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                chatService.post(new ChatMessage(((AspectJManagedUI) getUI()).getAuthor(), room, message.getValue()));
                message.setValue("");
                message.focus();
            }
        });
        post.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        bar.addComponent(post);
    }

    @Override
    public void attach() {
        super.attach();
        eventMulticaster.addApplicationListener(this);
    }

    @Override
    public void detach() {
        eventMulticaster.removeApplicationListener(this);
        super.detach();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        room = viewChangeEvent.getParameters();
        for (ChatMessage message : chatService.getMessagesInRoom(room)) {
            addMessage(message);
        }
    }

    private void addMessage(ChatMessage message) {
        messagesLayout.addComponent(new Label(String.format("%s %s: %s", message.getTimestamp(),
                message.getSender(), message.getMessage())));
    }

    @Override
    public void onApplicationEvent(MessagePostedEvent event) {
        if (event.getMessage().getRoom().equals(room)) {
            addMessage(event.getMessage());
        }
    }
}
