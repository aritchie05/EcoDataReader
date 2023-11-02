package com.apex.service;

import com.apex.model.*;

import java.sql.*;
import java.util.List;

public class DbService {

    private static final String DB_LOCATION =
            "jdbc:sqlite:C:\\Users\\aritc\\IdeaProjects\\EcoCraftingDataApi\\src\\main\\resources\\eco-data.db";

    private static final String PASSWORD = System.getenv("DB_PWD");

    private static final String SELECT_SKILL_NAME_ID_SQL = "SELECT ID, SkillName FROM Skills";
    private static final String SELECT_ITEM_NAME_ID_SQL = "SELECT ID, Name FROM Items";

    private static final String INSERT_ITEM_SQL = "INSERT INTO ITEMS(Name, ItemNameID, Tag) VALUES (?, ?, ?)";
    private static final String INSERT_RECIPE_SQL = "INSERT INTO RECIPES(RecipeName, RecipeNameID, SkillNameID, Level, " +
            "Labor, CraftingTableNameID) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_INGREDIENT_SQL = "INSERT INTO INGREDIENTS(RecipeNameID, ItemNameID, Quantity, " +
            "Reducible, Tag) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_OUTPUT_SQL = "INSERT INTO OUTPUTS(RecipeNameID, ItemNameID, Quantity, " +
            "Reducible) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CRAFTING_TABLE_SQL = "INSERT INTO CRAFTINGTABLES(Name, NameID, UpgradeModule) " +
            "VALUES (?, ?, ?)";

    private static final String UPDATE_SKILL_NAME_ID_SQL = "UPDATE SKILLS SET SkillNameID = ? WHERE ID = ?";
    private static final String UPDATE_ITEM_NAME_ID_SQL = "UPDATE ITEMS SET ItemNameID = ? WHERE ID = ?";

    private DbService() {
    }

    public static void insertCraftingTables(List<CraftingTable> tables) throws SQLException {
        Connection connection = getConnection();

        for (CraftingTable table : tables) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_CRAFTING_TABLE_SQL)) {
                statement.setString(1, table.getCraftingTableName());
                statement.setString(2, table.getCraftingTableNameID());
                statement.setString(3, table.getUpgradeModuleTag());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        connection.close();
    }

    public static void insertRecipes(List<Recipe> recipes) throws SQLException {
        Connection connection = getConnection();
        for (Recipe recipe : recipes) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_RECIPE_SQL)) {
                statement.setString(1, recipe.getName());
                statement.setString(2, recipe.getNameID());
                statement.setString(3, recipe.getSkillNameID());
                statement.setInt(4, recipe.getLevel());
                statement.setInt(5, recipe.getLabor());
                statement.setString(6, recipe.getCraftingTableNameID());
                statement.executeUpdate();
            }
            for (Ingredient ingredient : recipe.getIngredients()) {
                try (PreparedStatement statement = connection.prepareStatement(INSERT_INGREDIENT_SQL)) {
                    statement.setString(1, recipe.getNameID());
                    statement.setString(2, ingredient.getItemNameID());
                    statement.setBigDecimal(3, ingredient.getQuantity());
                    statement.setBoolean(4, ingredient.isReducible());
                    statement.setBoolean(5, ingredient.isTag());
                    statement.executeUpdate();
                }
            }
            for (Output output : recipe.getOutputs()) {
                try (PreparedStatement statement = connection.prepareStatement(INSERT_OUTPUT_SQL)) {
                    statement.setString(1, recipe.getNameID());
                    statement.setString(2, output.getItemNameID());
                    statement.setBigDecimal(3, output.getQuantity());
                    statement.setBoolean(4, output.isReducible());
                    statement.executeUpdate();
                }
            }
        }
        connection.close();
    }

    /**
     * Inserts an Item to the database
     * @param item the item to insert
     * @throws SQLException on sql errors
     */
    public static void insertItem(Item item, boolean tag) throws SQLException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_ITEM_SQL)) {
            statement.setString(1, item.getName());
            statement.setString(2, item.getItemNameID());
            statement.setBoolean(3, tag);
            statement.executeUpdate();
        }
        connection.close();
    }

    public static void insertItems(List<Item> items, boolean tags) throws SQLException {
        Connection connection = getConnection();

        for (Item item : items) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_ITEM_SQL)) {
                statement.setString(1, item.getName());
                statement.setString(2, item.getItemNameID());
                statement.setBoolean(3, tags);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        connection.close();
    }


    /**
     * Adds the Skill name IDs to the database based on the skill display name
     * @throws SQLException on sql errors
     */
    public static void addSkillNameIDs() throws SQLException {
        Connection connection = getConnection();
        ResultSet resultSet;
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(SELECT_SKILL_NAME_ID_SQL);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String skillName = resultSet.getString("SkillName");
                String skillNameId = skillName.replace(" ", "") + "Skill";
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_SKILL_NAME_ID_SQL)) {
                    updateStatement.setString(1, skillNameId);
                    updateStatement.setInt(2, id);
                    updateStatement.executeUpdate();
                }
            }
        }

        connection.close();
    }

    /**
     * Adds the Item name IDs to the database based on the item display name
     * @throws SQLException on sql errors
     */
    public static void addItemNameIDs() throws SQLException {
        Connection connection = getConnection();
        ResultSet resultSet;
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(SELECT_ITEM_NAME_ID_SQL);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String itemName = resultSet.getString("Name");
                String itemNameID = itemName.replace(" ", "") + "Item";
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_ITEM_NAME_ID_SQL)) {
                    updateStatement.setString(1, itemNameID);
                    updateStatement.setInt(2, id);
                    updateStatement.executeUpdate();
                }
            }
        }

        connection.close();
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_LOCATION, null, PASSWORD);
    }
}
