/*
 * Copyright (C) 2012-2013 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.aggregate.odktables.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.opendatakit.aggregate.odktables.relation.DbColumnDefinitions.DbColumnDefinitionsEntity;
import org.opendatakit.aggregate.odktables.relation.DbKeyValueStore.DbKeyValueStoreEntity;
import org.opendatakit.aggregate.odktables.relation.DbTableAcl.DbTableAclEntity;
import org.opendatakit.aggregate.odktables.relation.DbTableDefinitions.DbTableDefinitionsEntity;
import org.opendatakit.aggregate.odktables.relation.DbTableEntry.DbTableEntryEntity;
import org.opendatakit.aggregate.odktables.relation.DbTableFileInfo.DbTableFileInfoEntity;
import org.opendatakit.aggregate.odktables.rest.TableConstants;
import org.opendatakit.aggregate.odktables.rest.entity.Column;
import org.opendatakit.aggregate.odktables.rest.entity.OdkTablesKeyValueStoreEntry;
import org.opendatakit.aggregate.odktables.rest.entity.Row;
import org.opendatakit.aggregate.odktables.rest.entity.Scope;
import org.opendatakit.aggregate.odktables.rest.entity.TableAcl;
import org.opendatakit.aggregate.odktables.rest.entity.TableDefinition;
import org.opendatakit.aggregate.odktables.rest.entity.TableEntry;
import org.opendatakit.aggregate.odktables.rest.entity.TableProperties;
import org.opendatakit.aggregate.odktables.rest.entity.TableRole;
import org.opendatakit.aggregate.odktables.rest.entity.TableType;
import org.opendatakit.common.ermodel.Entity;
import org.opendatakit.common.persistence.DataField;
import org.opendatakit.common.persistence.DataField.DataType;

/**
 * Converts between datastore {@link Entity} objects and domain objects in
 * org.opendatakit.aggregate.odktables.entity.
 *
 * @author the.dylan.price@gmail.com
 * @author sudar.sam@gmail.com
 *
 */

public class EntityConverter {

  /**
   * Convert a {@link DbTableEntry} entity to a {@link TableEntry}
   */
  public TableEntry toTableEntry(DbTableEntryEntity entity) {
    String tableId = entity.getId();
    String tableKey = entity.getTableKey();
    String dataEtag = entity.getDataETag();
    String propertiesEtag = entity.getPropertiesETag();
    TableEntry entry = new TableEntry(tableId, tableKey, dataEtag,
        propertiesEtag);
    return entry;
  }

  /**
   * Convert a list of {@link DbTableEntry} entities to a list of
   * {@link TableEntry}
   *
   * @param entities
   *          the entities to convert
   */
  public List<TableEntry> toTableEntries(List<DbTableEntryEntity> entities) {
    ArrayList<TableEntry> entries = new ArrayList<TableEntry>();
    if ( entities != null ) {
      for (DbTableEntryEntity entity : entities ) {
        entries.add(toTableEntry(entity));
      }
    }
    return entries;
  }

  /**
   * Convert a {@link DbColumnDefinitions} entity to a {@link Column}
   */
  public Column toColumn(DbColumnDefinitionsEntity entity) {
    String tableId = entity.getTableId();
    String elementKey = entity.getElementKey();
    String elementName = entity.getElementName();
    String elementTypeStr = entity.getElementType();
    String listChildElementKeys =
        entity.getListChildElementKeys();
    Boolean isPersisted = entity.getIsPersisted();
    String joins = entity.getJoins();
    Column column = new Column(tableId, elementKey, elementName,
        elementTypeStr, listChildElementKeys, isPersisted, joins);
    return column;
  }

  /**
   * Convert a list of {@link DbColumnDefinitions} entities to a list of
   * {@link Column} objects.
   */
  public List<Column> toColumns(List<DbColumnDefinitionsEntity> entities) {
    List<Column> columns = new ArrayList<Column>();
    for (DbColumnDefinitionsEntity entity : entities) {
      columns.add(toColumn(entity));
    }
    return columns;
  }

  public TableProperties toTableProperties(List<DbKeyValueStoreEntity> kvsEntities,
      String tableId, String propertiesEtag) {
    List<OdkTablesKeyValueStoreEntry> kvsEntries =
        toOdkTablesKeyValueStoreEntry(kvsEntities);
    TableProperties properties = new TableProperties(propertiesEtag, tableId,
        kvsEntries);
    return properties;
  }

  public OdkTablesKeyValueStoreEntry toOdkTablesKeyValueStoreEntry(
      DbKeyValueStoreEntity entity) {
    String tableId = entity.getTableId();
    String partition = entity.getPartition();
    String aspect = entity.getAspect();
    String key = entity.getKey();
    String type = entity.getType();
    String value = entity.getValue();
    OdkTablesKeyValueStoreEntry entry = new OdkTablesKeyValueStoreEntry();
    entry.tableId = tableId;
    entry.partition = partition;
    entry.aspect = aspect;
    entry.key = key;
    entry.type = type;
    entry.value = value;
    return entry;
  }

  /**
   * Return a TableDefinition based upon the {@link DbTableDefinitionsEntity} parameter, which
   * must have been generated from the {@link DbTableDefinitions} relation.
   * All fields are from the entity except the columns, which are set to null.
   * @param definitionEntity
   * @return
   */
  public TableDefinition toTableDefinition(TableEntry entryEntity, DbTableDefinitionsEntity definitionEntity) {
    String tableId = definitionEntity.getTableId();
    String tableKey = entryEntity.getTableKey();
    String dbTableName = definitionEntity.getDbTableName();
    String tableTypeStr = definitionEntity.getType();
    TableType tableType = TableType.valueOf(tableTypeStr);
    String tableIdAccessControls = definitionEntity.getTableIdAccessControls();
    return new TableDefinition(tableId, null, tableKey, dbTableName, tableType,
        tableIdAccessControls);
  }

  public List<OdkTablesKeyValueStoreEntry> toOdkTablesKeyValueStoreEntry(
      List<DbKeyValueStoreEntity> kvsEntities) {
    List<OdkTablesKeyValueStoreEntry> kvsEntries =
        new ArrayList<OdkTablesKeyValueStoreEntry>();
    for (DbKeyValueStoreEntity entity : kvsEntities) {
      kvsEntries.add(toOdkTablesKeyValueStoreEntry(entity));
    }
    return kvsEntries;
  }

  /**
   * Convert a {@link DbTableAcl} entity to a {@link TableAcl}
   */
  public TableAcl toTableAcl(DbTableAclEntity entity) {
    Scope.Type scopeType = Scope.Type.valueOf(entity.getScopeType());
    String scopeValue = entity.getScopeValue();
    Scope scope = new Scope(scopeType, scopeValue);
    TableRole role = TableRole.valueOf(entity.getRole());
    TableAcl acl = new TableAcl();
    acl.setRole(role);
    acl.setScope(scope);
    return acl;
  }

  /**
   * Convert a list of {@link DbTableAcl} entities to a list of {@link TableAcl}
   * .
   */
  public List<TableAcl> toTableAcls(List<DbTableAclEntity> entities) {
    List<TableAcl> acls = new ArrayList<TableAcl>();
    for (DbTableAclEntity entity : entities) {
      TableAcl acl = toTableAcl(entity);
      acls.add(acl);
    }
    return acls;
  }

  /**
   * Convert a {@link Column} to a {@link DataField}
   */
  public DataField toField(Column column) {
    // ss: exactly what the point of this method is eludes me. However, I
    // believe that the "type" of an ODK Tables Column on the aggregate side
    // is always a string. Tables permits more complicated types like image,
    // location, etc, and therefore there is no way/reason to map each level
    // to the aggregate side.
    DataField field = new DataField(RUtil.convertIdentifier(column.getElementKey()),
        DataType.STRING, true);
    return field;
  }

  /**
   * Convert a {@link DbColumnDefinitions} entity to a {@link DataField}
   */
  public DataField toField(DbColumnDefinitionsEntity entity) {
    // Note that here is where Aggregate is deciding that all the column types
    // in the user-defined columns are in fact of type DataType.STRING.
    // Therefore we're not allowing any sort of more fancy number searching or
    // anything like that on the server. This should eventually map more
    // intelligently. It is not being done at this point because exactly what
    // we do in the case of changing table properties is not defined. Therefore
    // if someone was to start out with a number, and aggregate had the column
    // type as a number, and they decided to change it, there could be issues.
    // This whole process needs to be more thought out. But for now, they're
    // remaining all types as a String.
    // TODO: make map ODKTables column types to the appropriate aggregate type.
    DataField field = new DataField(RUtil.convertIdentifier(entity.getId()),
        DataType.STRING, true);
    return field;
  }

  /**
   * Convert a list of {@link DbColumnDefinitions} entities to a list of {@link DataField}
   */
  public List<DataField> toFields(List<DbColumnDefinitionsEntity> entities) {
    List<DataField> fields = new ArrayList<DataField>();
    for (DbColumnDefinitionsEntity entity : entities)
      fields.add(toField(entity));
    return fields;
  }

  /**
   * Convert a {@link DbTable} entity into a {@link Row}. The returned row
   * will have the {@link DbTable} metadata columns such as timestamp and
   * row_version set.
   *
   * @param entity
   *          the {@link DbTable} entity.
   * @param columns
   *          the {@link DbColumnDefinitions} entities of the table
   * @return the row
   */
  public Row toRow(Entity entity, List<DbColumnDefinitionsEntity> columns) {
    Row row = new Row();
    row.setRowId(entity.getId());
    row.setRowEtag(entity.getString(DbTable.ROW_VERSION));
    row.setDataEtagAtModification(
        entity.getString(DbTable.DATA_ETAG_AT_MODIFICATION));
    row.setDeleted(entity.getBoolean(DbTable.DELETED));
    row.setCreateUser(entity.getString(DbTable.CREATE_USER));
    row.setLastUpdateUser(entity.getString(DbTable.LAST_UPDATE_USER));
    row.setUriUser(entity.getString(TableConstants.URI_ACCESS_CONTROL.toUpperCase()));
    row.setFormId(entity.getString(TableConstants.FORM_ID.toUpperCase()));
    row.setInstanceName(
        entity.getString(TableConstants.INSTANCE_NAME.toUpperCase()));
    row.setLocale(entity.getString(TableConstants.LOCALE.toUpperCase()));
    row.setTimestamp(entity.getDate(TableConstants.TIMESTAMP.toUpperCase()));
    String filterType = entity.getString(DbTable.FILTER_TYPE);
    if (filterType != null) {
      Scope.Type type = Scope.Type.valueOf(filterType);
      if (filterType.equals(Scope.Type.DEFAULT)) {
        row.setFilterScope(new Scope(Scope.Type.DEFAULT, null));
      } else {
        String value = entity.getString(DbTable.FILTER_VALUE);
        row.setFilterScope(new Scope(type, value));
      }
    } else {
      row.setFilterScope(Scope.EMPTY_SCOPE);
    }

    row.setValues(getRowValues(entity, columns));
    return row;
  }

  /**
   * This method creates a row from an entity retrieved from the
   * DbTableFileInfo table. It makes use of the static List of
   * String column names in that class.
   * @param entity
   * @return
   * @author sudar.sam@gmail.com
   */
  public static Row toRowFromFileInfo(DbTableFileInfoEntity entity) {
	  Row row = new Row();
	  row.setRowId(entity.getId());
	  row.setRowEtag(entity.getStringField(DbTable.ROW_VERSION));
     row.setDataEtagAtModification(entity.getStringField(DbTable.DATA_ETAG_AT_MODIFICATION));
	  row.setDeleted(entity.getBooleanField(DbTable.DELETED));
	  row.setCreateUser(entity.getStringField(DbTable.CREATE_USER));
	  row.setLastUpdateUser(entity.getStringField(DbTable.LAST_UPDATE_USER));
	  String filterType = entity.getStringField(DbTable.FILTER_TYPE);
	  if (filterType != null) {
	      Scope.Type type = Scope.Type.valueOf(filterType);
	      if (filterType.equals(Scope.Type.DEFAULT)) {
	      row.setFilterScope(new Scope(Scope.Type.DEFAULT, null));
	        } else {
	          String value = entity.getStringField(DbTable.FILTER_VALUE);
	          row.setFilterScope(new Scope(type, value));
	        }
	      } else {
	        row.setFilterScope(Scope.EMPTY_SCOPE);
	      }
	  // this will be the actual values of the row
	  Map<String, String> values = new HashMap<String, String>();
	  for (DataField column : DbTableFileInfo.exposedColumnNames) {
	    Validate.isTrue(column.getDataType() == DataType.STRING);
	    String value = entity.getStringField(column);
	    values.put(column.getName(), value);
	  }
	  row.setValues(values);
	  return row;
  }

  /**
   * Return a list of rows from a list of entities queried from the
   * DbTableFileInfo table. Just calls {@link toRowFromFileInfo()}
   * for every entity in the list. However, it does NOT include
   * deleted rows.
   * @param entities
   * @return
   */
  public static List<Row> toRowsFromFileInfo(List<DbTableFileInfoEntity> entities) {
	  List<Row> rows = new ArrayList<Row>();
	  for (DbTableFileInfoEntity e : entities) {
		  Row row = toRowFromFileInfo(e);
		  if (!row.isDeleted()) {
			  rows.add(row);
		  }
	  }
	  return rows;
  }

  /**
   * Convert a {@link DbLogTable} entity into a {@link Row}
   *
   * @param entity
   *          the {@link DbLogTable} entity.
   * @param columns
   *          the {@link DbColumnDefinitions} entities of the table
   * @return the row
   */
  public Row toRowFromLogTable(Entity entity, List<DbColumnDefinitionsEntity> columns) {
    Row row = new Row();
    row.setRowId(entity.getString(DbLogTable.ROW_ID));
    row.setRowEtag(entity.getString(DbLogTable.ROW_VERSION));
    row.setDataEtagAtModification(
        entity.getString(DbLogTable.DATA_ETAG_AT_MODIFICATION));
    row.setDeleted(entity.getBoolean(DbLogTable.DELETED));
    row.setCreateUser(entity.getString(DbLogTable.CREATE_USER));
    row.setLastUpdateUser(entity.getString(DbLogTable.LAST_UPDATE_USER));
    String filterType = entity.getString(DbLogTable.FILTER_TYPE);
    if (filterType != null) {
      Scope.Type type = Scope.Type.valueOf(filterType);
      if (type.equals(Scope.Type.DEFAULT)) {
        row.setFilterScope(new Scope(Scope.Type.DEFAULT, null));
      } else {
        String value = entity.getString(DbLogTable.FILTER_VALUE);
        row.setFilterScope(new Scope(type, value));
      }
    } else {
      row.setFilterScope(Scope.EMPTY_SCOPE);
    }

    row.setValues(getRowValues(entity, columns));
    return row;
  }

  private Map<String, String> getRowValues(Entity entity, List<DbColumnDefinitionsEntity> columns) {
    Map<String, String> values = new HashMap<String, String>();
    for (DbColumnDefinitionsEntity column : columns) {
      String name = column.getElementKey();
      String value = entity.getAsString(RUtil.convertIdentifier(column.getId()));
      values.put(name, value);
    }
    return values;
  }

  /**
   * Convert a list of {@link DbTable} or {@link DbLogTable} entities into a
   * list of {@link Row}
   *
   * @param entities
   *          the {@link DbTable} or {@link DbLogTable} entities
   * @param columns
   *          the {@link DbColumnDefinitions} of the table
   * @param fromLogTable
   *          true if the rows are from the {@link DbLogTable}
   * @return the converted rows
   */
  public List<Row> toRows(List<Entity> entities, List<DbColumnDefinitionsEntity> columns,
      boolean fromLogTable) {
    ArrayList<Row> rows = new ArrayList<Row>();
    for (Entity entity : entities) {
      if (fromLogTable)
        rows.add(toRowFromLogTable(entity, columns));
      else
        rows.add(toRow(entity, columns));
    }
    return rows;
  }

}