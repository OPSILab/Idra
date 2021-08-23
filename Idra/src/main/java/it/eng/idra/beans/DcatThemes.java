/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatThemes.
 */
@Entity
@Table(name = "themes")
public class DcatThemes {

  /** The identifier. */
  @Id
  private String identifier;

  /** The bg. */
  @Lob
  private String bg;

  /** The cs. */
  @Lob
  private String cs;

  /** The da. */
  @Lob
  private String da;

  /** The de. */
  @Lob
  private String de;

  /** The el. */
  @Lob
  private String el;

  /** The en. */
  @Lob
  private String en;

  /** The es. */
  @Lob
  private String es;

  /** The et. */
  @Lob
  private String et;

  /** The fi. */
  @Lob
  private String fi;

  /** The fr. */
  @Lob
  private String fr;

  /** The ga. */
  @Lob
  private String ga;

  /** The hr. */
  @Lob
  private String hr;

  /** The hu. */
  @Lob
  private String hu;

  /** The it. */
  @Lob
  private String it;

  /** The lt. */
  @Lob
  private String lt;

  /** The lv. */
  @Lob
  private String lv;

  /** The mt. */
  @Lob
  private String mt;

  /** The nl. */
  @Lob
  private String nl;

  /** The no. */
  @Lob
  private String no;

  /** The pl. */
  @Lob
  private String pl;

  /** The pt. */
  @Lob
  private String pt;

  /** The ro. */
  @Lob
  private String ro;

  /** The sk. */
  @Lob
  private String sk;

  /** The sl. */
  @Lob
  private String sl;

  /** The sv. */
  @Lob
  private String sv;

  /**
   * Instantiates a new dcat themes.
   */
  public DcatThemes() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Gets the identifier.
   *
   * @return the identifier
   */
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * Sets the identifier.
   *
   * @param identifier the new identifier
   */
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the bg.
   *
   * @return the bg
   */
  public String getBg() {
    return this.bg;
  }

  /**
   * Sets the bg.
   *
   * @param bg the new bg
   */
  public void setBg(String bg) {
    this.bg = bg;
  }

  /**
   * Gets the cs.
   *
   * @return the cs
   */
  public String getCs() {
    return this.cs;
  }

  /**
   * Sets the cs.
   *
   * @param cs the new cs
   */
  public void setCs(String cs) {
    this.cs = cs;
  }

  /**
   * Gets the da.
   *
   * @return the da
   */
  public String getDa() {
    return this.da;
  }

  /**
   * Sets the da.
   *
   * @param da the new da
   */
  public void setDa(String da) {
    this.da = da;
  }

  /**
   * Gets the de.
   *
   * @return the de
   */
  public String getDe() {
    return this.de;
  }

  /**
   * Sets the de.
   *
   * @param de the new de
   */
  public void setDe(String de) {
    this.de = de;
  }

  /**
   * Gets the el.
   *
   * @return the el
   */
  public String getEl() {
    return this.el;
  }

  /**
   * Sets the el.
   *
   * @param el the new el
   */
  public void setEl(String el) {
    this.el = el;
  }

  /**
   * Gets the en.
   *
   * @return the en
   */
  public String getEn() {
    return this.en;
  }

  /**
   * Sets the en.
   *
   * @param en the new en
   */
  public void setEn(String en) {
    this.en = en;
  }

  /**
   * Gets the es.
   *
   * @return the es
   */
  public String getEs() {
    return this.es;
  }

  /**
   * Sets the es.
   *
   * @param es the new es
   */
  public void setEs(String es) {
    this.es = es;
  }

  /**
   * Gets the et.
   *
   * @return the et
   */
  public String getEt() {
    return this.et;
  }

  /**
   * Sets the et.
   *
   * @param et the new et
   */
  public void setEt(String et) {
    this.et = et;
  }

  /**
   * Gets the fi.
   *
   * @return the fi
   */
  public String getFi() {
    return this.fi;
  }

  /**
   * Sets the fi.
   *
   * @param fi the new fi
   */
  public void setFi(String fi) {
    this.fi = fi;
  }

  /**
   * Gets the fr.
   *
   * @return the fr
   */
  public String getFr() {
    return this.fr;
  }

  /**
   * Sets the fr.
   *
   * @param fr the new fr
   */
  public void setFr(String fr) {
    this.fr = fr;
  }

  /**
   * Gets the ga.
   *
   * @return the ga
   */
  public String getGa() {
    return this.ga;
  }

  /**
   * Sets the ga.
   *
   * @param ga the new ga
   */
  public void setGa(String ga) {
    this.ga = ga;
  }

  /**
   * Gets the hr.
   *
   * @return the hr
   */
  public String getHr() {
    return this.hr;
  }

  /**
   * Sets the hr.
   *
   * @param hr the new hr
   */
  public void setHr(String hr) {
    this.hr = hr;
  }

  /**
   * Gets the hu.
   *
   * @return the hu
   */
  public String getHu() {
    return this.hu;
  }

  /**
   * Sets the hu.
   *
   * @param hu the new hu
   */
  public void setHu(String hu) {
    this.hu = hu;
  }

  /**
   * Gets the it.
   *
   * @return the it
   */
  public String getIt() {
    return this.it;
  }

  /**
   * Sets the it.
   *
   * @param it the new it
   */
  public void setIt(String it) {
    this.it = it;
  }

  /**
   * Gets the lt.
   *
   * @return the lt
   */
  public String getLt() {
    return this.lt;
  }

  /**
   * Sets the lt.
   *
   * @param lt the new lt
   */
  public void setLt(String lt) {
    this.lt = lt;
  }

  /**
   * Gets the lv.
   *
   * @return the lv
   */
  public String getLv() {
    return this.lv;
  }

  /**
   * Sets the lv.
   *
   * @param lv the new lv
   */
  public void setLv(String lv) {
    this.lv = lv;
  }

  /**
   * Gets the mt.
   *
   * @return the mt
   */
  public String getMt() {
    return this.mt;
  }

  /**
   * Sets the mt.
   *
   * @param mt the new mt
   */
  public void setMt(String mt) {
    this.mt = mt;
  }

  /**
   * Gets the nl.
   *
   * @return the nl
   */
  public String getNl() {
    return this.nl;
  }

  /**
   * Sets the nl.
   *
   * @param nl the new nl
   */
  public void setNl(String nl) {
    this.nl = nl;
  }

  /**
   * Gets the no.
   *
   * @return the no
   */
  public String getNo() {
    return this.no;
  }

  /**
   * Sets the no.
   *
   * @param no the new no
   */
  public void setNo(String no) {
    this.no = no;
  }

  /**
   * Gets the pl.
   *
   * @return the pl
   */
  public String getPl() {
    return this.pl;
  }

  /**
   * Sets the pl.
   *
   * @param pl the new pl
   */
  public void setPl(String pl) {
    this.pl = pl;
  }

  /**
   * Gets the pt.
   *
   * @return the pt
   */
  public String getPt() {
    return this.pt;
  }

  /**
   * Sets the pt.
   *
   * @param pt the new pt
   */
  public void setPt(String pt) {
    this.pt = pt;
  }

  /**
   * Gets the ro.
   *
   * @return the ro
   */
  public String getRo() {
    return this.ro;
  }

  /**
   * Sets the ro.
   *
   * @param ro the new ro
   */
  public void setRo(String ro) {
    this.ro = ro;
  }

  /**
   * Gets the sk.
   *
   * @return the sk
   */
  public String getSk() {
    return this.sk;
  }

  /**
   * Sets the sk.
   *
   * @param sk the new sk
   */
  public void setSk(String sk) {
    this.sk = sk;
  }

  /**
   * Gets the sl.
   *
   * @return the sl
   */
  public String getSl() {
    return this.sl;
  }

  /**
   * Sets the sl.
   *
   * @param sl the new sl
   */
  public void setSl(String sl) {
    this.sl = sl;
  }

  /**
   * Gets the sv.
   *
   * @return the sv
   */
  public String getSv() {
    return this.sv;
  }

  /**
   * Sets the sv.
   *
   * @param sv the new sv
   */
  public void setSv(String sv) {
    this.sv = sv;
  }

}
