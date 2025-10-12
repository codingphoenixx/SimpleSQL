package dev.coph.simplesql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public record SimpleResultSet(ResultSet resultSet) {

    public SimpleResultSet next(ResultSetConsumer consumer) throws SQLException {
        if (resultSet.next())
            consumer.accept(resultSet);
        return this;
    }

    public SimpleResultSet next(ResultSetConsumer consumer, EmptyResultSetConsumer emptyConsumer) throws SQLException {
        if (resultSet.next())
            consumer.accept(resultSet);
        else
            emptyConsumer.accept(resultSet);
        return this;
    }

    public SimpleResultSet forEach(ResultSetConsumer consumer) throws SQLException {

        while (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public SimpleResultSet forEach(ResultSetConsumer consumer, EmptyResultSetConsumer emptyConsumer) throws SQLException {
        int count = 0;

        while (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }

        if (count == 0)
            emptyConsumer.accept(resultSet);
        return this;
    }

    public SimpleResultSet toBeginning() throws SQLException {
        if (resultSet.isBeforeFirst())
            return this;
        resultSet.beforeFirst();
        return this;
    }

    public boolean isEmpty() throws SQLException {
        if (resultSet == null) {
            return true;
        }

        int currentRow = resultSet.getRow();
        boolean wasBeforeFirst = resultSet.isBeforeFirst();
        boolean wasAfterLast = resultSet.isAfterLast();

        boolean isEmpty = !resultSet.first();

        if (isEmpty) {
            resultSet.beforeFirst();
        } else {
            if (wasBeforeFirst) {
                resultSet.beforeFirst();
            } else if (wasAfterLast) {
                resultSet.afterLast();
            } else if (currentRow > 0) {
                resultSet.absolute(currentRow);
            } else {
                resultSet.beforeFirst();
            }
        }

        return isEmpty;
    }

    public interface EmptyResultSetConsumer {
        void accept(ResultSet resultSet) throws SQLException;
    }

    public interface ResultSetConsumer {
        void accept(ResultSet resultSet) throws SQLException;
    }
}
