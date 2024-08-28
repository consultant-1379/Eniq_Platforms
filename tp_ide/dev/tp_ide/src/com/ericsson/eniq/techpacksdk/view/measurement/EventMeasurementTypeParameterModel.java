package com.ericsson.eniq.techpacksdk.view.measurement;

import java.util.List;
import java.util.Vector;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.PairComponent;
import tableTreeUtils.TCTableModel;

import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

public class EventMeasurementTypeParameterModel extends MeasurementTypeParameterModel {

  protected static final String ONE_MIN_AGG = "One Minute Aggregation";

  protected static final String FIFTEEN_MIN_AGG = "Fifteen Minute Aggregation";

  protected static final String LOAD_FILE_DUP_CHECK = "File Duplicate Check";

  protected static final String EVENTS_CALC = "Events Calc Table";

  protected static final String MIX_PARTITIONS = "Mixed-Partitions Table";

  PairComponent compOneMinAgg;

  PairComponent compFifteenMinAgg;

  PairComponent compLoadFileDupCheck;

  private final int eventsDefaultWidth = 1000;

  public EventMeasurementTypeParameterModel(final Application application, final Versioning versioning,
      final List<Measurementtypeclass> mtypeclasses, final MeasurementtypeExt measurementtypeExt,
      final TCTableModel dcsupportTableModel, final TCTableModel objectbhTableModel, final String[] universeExtensions,
      final RockFactory rockFactory, final boolean isTreeEditable) {
    super(application, versioning, mtypeclasses, measurementtypeExt, dcsupportTableModel, objectbhTableModel,
        universeExtensions, rockFactory, isTreeEditable);
  }

  @Override
  protected int initializeSelectedTableTypeButton() {
    int selected = super.initializeSelectedTableTypeButton();

    if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()).intValue() == 1) {
      selected = 5;
    } else if (Utils.replaceNull(getMeasurementtypeExt().getMixedpartitionstable()).intValue() == 1) {
      selected = 6;
    }

    return selected;
  }

  @Override
  protected List<String> initializeTableTypeButtons() {
    final List<String> buttons = super.initializeTableTypeButtons();

    buttons.add(EVENTS_CALC);
    buttons.add(MIX_PARTITIONS);
    return buttons;

  }

  @Override
  protected void initializeCheckBoxes() {
    super.initializeCheckBoxes();
    compOneMinAgg = addCheckBox(ONE_MIN_AGG, Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue() == 1);
    compFifteenMinAgg = addCheckBox(FIFTEEN_MIN_AGG, Utils.replaceNull(getMeasurementtypeExt().getFifteenminagg())
        .intValue() == 1);
    compLoadFileDupCheck = addCheckBox(LOAD_FILE_DUP_CHECK, Utils.replaceNull(
        getMeasurementtypeExt().getLoadfile_dup_check()).intValue() == 1);
  }

  @Override
  protected void setEnabelesAndDisables(final int selected) {
    super.setEnabelesAndDisables(selected);

    if (selected == 5 || selected == 6) {
      compObjectBHSupport.getComponent().setEnabled(false);
      compClassification.getComponent().setEnabled(false);
      compUniverseExt.getComponent().setEnabled(false);
      compDescription.getComponent().setEnabled(false);
    }

  }

  @Override
  public Object getValueAt(final String identifier) {
    Object value = super.getValueAt(identifier);

    if (value == null) {
      if (ONE_MIN_AGG.equals(identifier)) {
        value = Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue();
      } else if (FIFTEEN_MIN_AGG.equals(identifier)) {
        value = Utils.replaceNull(getMeasurementtypeExt().getFifteenminagg()).intValue();
      } else if (LOAD_FILE_DUP_CHECK.equals(identifier)) {
        value = Utils.replaceNull(getMeasurementtypeExt().getLoadfile_dup_check()).intValue();
      }
    }
    return value;
  }

  @Override
  protected Object getTableType() {
    Object tableType = super.getTableType();

    if (tableType == null) {
      if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()) == 1) {
        tableType = EVENTS_CALC;
      } else if (Utils.replaceNull(getMeasurementtypeExt().getMixedpartitionstable()) == 1) {
        tableType = MIX_PARTITIONS;
      }
    }
    return tableType;
  }

  @Override
  public void setValueAt(final Object value, final String identifier) {
    super.setValueAt(value, identifier);

    if (ONE_MIN_AGG.equals(identifier)) {
      getMeasurementtypeExt().setOneminagg(Utils.booleanToInteger((Boolean) value));
    } else if (FIFTEEN_MIN_AGG.equals(identifier)) {
      getMeasurementtypeExt().setFifteenminagg(Utils.booleanToInteger((Boolean) value));
    } else if (LOAD_FILE_DUP_CHECK.equals(identifier)) {
      getMeasurementtypeExt().setLoadfile_dup_check(Utils.booleanToInteger((Boolean) value));
    }
  }

  @Override
  protected int setTableType(final Object value) {
    int selected = super.setTableType(value);

    getMeasurementtypeExt().setEventscalctable(0);
    getMeasurementtypeExt().setMixedpartitionstable(0);

    if (value != null) {
      if (value.equals(EVENTS_CALC)) {
        getMeasurementtypeExt().setEventscalctable(1);
        selected = 5;
      } else if (value.equals(MIX_PARTITIONS)) {
        getMeasurementtypeExt().setMixedpartitionstable(1);
        selected = 6;
      }
    }
    return selected;
  }

  @Override
  public Vector<String> validateData() {
    final Vector<String> errorStrings = super.validateData();

    // If 'Events Calc Table' is selected, then at least one from 'Total
    // Aggregation', 'One Minute Aggregation' or 'Fifteen Minute Aggregation'
    // must be selected.
    if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()).intValue() == 1
        && Utils.replaceNull(getMeasurementtypeExt().getTotalagg()).intValue() == 0
        && Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue() == 0
        && Utils.replaceNull(getMeasurementtypeExt().getFifteenminagg()).intValue() == 0) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If " + EVENTS_CALC
          + " is selected, then at least one from '" + TOTAL_AGG + "', '" + ONE_MIN_AGG + "', or '" + FIFTEEN_MIN_AGG
          + "' is also required.");
    }

    if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()).intValue() == 0
        && Utils.replaceNull(getMeasurementtypeExt().getFifteenminagg()).intValue() == 1) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + FIFTEEN_MIN_AGG + "' is selected, "
          + EVENTS_CALC + " is also required.");
    }

    if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()).intValue() == 0
        && Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue() == 1) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + ONE_MIN_AGG + "' is selected, " + EVENTS_CALC
          + " is also required.");
    }

    if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()).intValue() == 1
        && Utils.replaceNull(getMeasurementtypeExt().getElementbhsupport()).intValue() == 1) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + EVENTS_CALC + "' is selected, then '"
          + ELEM_BH + "' cannot be selected.");
    }

    if (Utils.replaceNull(getMeasurementtypeExt().getEventscalctable()).intValue() == 1
        && Utils.replaceNull(getMeasurementtypeExt().getDeltacalcsupport()).intValue() == 1) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + EVENTS_CALC + "' is selected, then '"
          + DELTA_CALC_SUPPORT + "' cannot be selected.");
    }

    // If One Min Agg or Fifteen Min agg is selected, then sizing must be sgeh
    if ((Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue() == 1 || Utils.replaceNull(
        getMeasurementtypeExt().getFifteenminagg()).intValue() == 1)
        && !(getMeasurementtypeExt().getSizing().equals(Constants.SGEH)
            || getMeasurementtypeExt().getSizing().equals(Constants.SGEHLARGE)
            || getMeasurementtypeExt().getSizing().equals(Constants.SGEHMEDIUM)
            || getMeasurementtypeExt().getSizing().equals(Constants.SGEHSMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.SGEHEXTRASMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.SGEHEXTRALARGE)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOLEXTRASMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOLSMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOLMEDIUM)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOLLARGE)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOLEXTRALARGE)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL2EXTRASMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL2SMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL2)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL3EXTRASMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL3SMALL)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL3)
            || getMeasurementtypeExt().getSizing().equals(Constants.VOL3MEDIUM) || getMeasurementtypeExt().getSizing()
            .equals(Constants.VOL3LARGE))) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + ONE_MIN_AGG + "', or '" + FIFTEEN_MIN_AGG
          + "' is selected, then sizing must be " + Constants.SGEH + " or " + Constants.SGEHLARGE + " or "
          + Constants.SGEHMEDIUM + " or " + Constants.SGEHSMALL + " or " + Constants.SGEHEXTRALARGE + " or "
          + Constants.SGEHEXTRASMALL);
    }

    if (Utils.replaceNull(getMeasurementtypeExt().getMixedpartitionstable()).intValue() == 1
        && !getMeasurementtypeExt().getSizing().equals(Constants.SGEH)) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + MIX_PARTITIONS
          + "' is selected, then sizing must be " + Constants.SGEH);
    }

    if (Utils.replaceNull(getMeasurementtypeExt().getMixedpartitionstable()).intValue() == 1
        && (Utils.replaceNull(getMeasurementtypeExt().getTotalagg()).intValue() == 1
            || Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue() == 1
            || Utils.replaceNull(getMeasurementtypeExt().getFifteenminagg()).intValue() == 1
            || Utils.replaceNull(getMeasurementtypeExt().getElementbhsupport()).intValue() == 1 || Utils.replaceNull(
            getMeasurementtypeExt().getDeltacalcsupport()).intValue() == 1)) {
      errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + MIX_PARTITIONS
          + "' is selected, then no aggregations can be choosen");
      
      if (Utils.replaceNull(getMeasurementtypeExt().getLoadfile_dup_check()).intValue() == 1
          && (Utils.replaceNull(getMeasurementtypeExt().getTotalagg()).intValue() == 1
              || Utils.replaceNull(getMeasurementtypeExt().getOneminagg()).intValue() == 1
              || Utils.replaceNull(getMeasurementtypeExt().getFifteenminagg()).intValue() == 1
              || Utils.replaceNull(getMeasurementtypeExt().getElementbhsupport()).intValue() == 1 || Utils.replaceNull(
              getMeasurementtypeExt().getDeltacalcsupport()).intValue() == 1)) {
        errorStrings.add(getMeasurementtypeExt().getTypename() + ": If '" + LOAD_FILE_DUP_CHECK
            + "' is selected, then no aggregations can be choosen");
      }
    }
    return errorStrings;
  }

  @Override
  protected String[] getSizingItems() {
    return Constants.EVENTSIZINGITEMS;
  }

  @Override
  protected int getDefaultWidth() {
    return eventsDefaultWidth;
  }
}
