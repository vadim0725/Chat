package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient extends Client {

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100.0);
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (!message.contains(":"))
                return;
            String[] strings = message.split(":");
            Date data = new Date();
            if (strings[1].contains("дата"))
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("d.MM.YYYY").format(data));
            else if (strings[1].contains("день")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("d").format(data));
            } else if (strings[1].contains("месяц")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("MMMM").format(data));
            } else if (strings[1].contains("год")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("YYYY").format(data));
            } else if (strings[1].contains("время")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("H:mm:ss").format(data));
            } else if (strings[1].contains("час")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("H").format(data));
            } else if (strings[1].contains("минуты")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("m").format(data));
            } else if (strings[1].contains("секунды")) {
                sendTextMessage("Информация для " + strings[0] + ": " + new SimpleDateFormat("s").format(data));
            }
        }
    }
}
