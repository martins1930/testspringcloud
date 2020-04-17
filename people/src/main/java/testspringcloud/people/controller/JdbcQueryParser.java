package testspringcloud.people.controller;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JdbcQueryParser {

  public String getWhereCondition(String search, ResourceController.TableMetaData tableMetaData) {
    Node rootNode = new RSQLParser().parse(search);
    return rootNode.accept(new JdbcVisitor(tableMetaData));
  }

  private static class JdbcVisitor implements RSQLVisitor<String, Object> {

    private final ResourceController.TableMetaData tableMetaData;

    public JdbcVisitor(ResourceController.TableMetaData tableMetaData) {
      this.tableMetaData = tableMetaData;
    }

    @Override
    public String visit(AndNode node, Object param) {
      return visitLogicalNode(node, param);
    }

    @Override
    public String visit(OrNode node, Object param) {
      return visitLogicalNode(node, param);
    }

    public String visitLogicalNode(LogicalNode node, Object param) {
      List<Node> childList = node.getChildren();
      List<String> nodeConditions = new LinkedList<>();
      if (childList != null) {
        for (Node childNode : childList) {
          String childNodeCondition = "";
          if (childNode instanceof OrNode) {
            childNodeCondition = visit((OrNode) childNode, param);
          } else if (childNode instanceof AndNode) {
            childNodeCondition = visit((AndNode) childNode, param);
          } else {
            childNodeCondition = visit((ComparisonNode) childNode, param);
          }
          nodeConditions.add(childNodeCondition);
        }
      }
      return "(" + String.join(getLogicalOperator(node), nodeConditions) + ")";
    }

    private String getLogicalOperator(LogicalNode node) {
      if (node instanceof AndNode) {
        return " AND ";
      } else {
        return " OR ";
      }
    }

    @Override
    public String visit(ComparisonNode node, Object param) {
      String databaseColumn = getSelectorAsDatabaseColumn(node);
      return databaseColumn
          + getOperatorAsString(node.getOperator())
          + getValues(databaseColumn, node.getArguments());
    }

    private String getSelectorAsDatabaseColumn(ComparisonNode node) {
      return tableMetaData.getDocumentNameToColumnName().get(node.getSelector());
    }

    private String getOperatorAsString(ComparisonOperator operator) {
      if (operator.equals(RSQLOperators.EQUAL)) {
        return "=";
      } else if (operator.equals(RSQLOperators.NOT_EQUAL)) {
        return "<>";
      } else if (operator.equals(RSQLOperators.GREATER_THAN)) {
        return ">";
      } else if (operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
        return ">=";
      } else if (operator.equals(RSQLOperators.LESS_THAN)) {
        return "<";
      } else if (operator.equals(RSQLOperators.LESS_THAN_OR_EQUAL)) {
        return "<=";
      } else if (operator.equals(RSQLOperators.IN)) {
        return " in ";
      } else if (operator.equals(RSQLOperators.NOT_IN)) {
        return " not in ";
      } else {
        throw new IllegalArgumentException(
            "Error the operator " + operator.getSymbol() + " doesn't exists.");
      }
    }

    private String getValues(String selector, List<String> arguments) {
      if (arguments.size() > 1) {
        return "("
            + arguments.stream()
            .map(value -> formatValueForType(selector, value))
            .collect(Collectors.joining(","))
            + ")";
      } else {
        return formatValueForType(selector, arguments.get(0));
      }
    }

    private String formatValueForType(String selector, String value) {
      Map<String, Class<?>> columnNameToFieldType = tableMetaData.getColumnNameToFieldType();
      if (columnNameToFieldType.get(selector).equals(String.class)) {
        return "'" + value + "'";
      } else {
        return value;
      }
    }
  }
}
