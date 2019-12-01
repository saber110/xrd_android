package com.example.dao;

public class Position {
    String text;
    int row;
    int column;
    public Position(String text,int row,int column){
        this.text=text;
        this.row=row;
        this.column=column;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
