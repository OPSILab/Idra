/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.eng.idra.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "themes")
public class DcatThemes {
  @Id
  private String identifier;

  @Lob
  private String bg;

  @Lob
  private String cs;

  @Lob
  private String da;

  @Lob
  private String de;

  @Lob
  private String el;

  @Lob
  private String en;

  @Lob
  private String es;

  @Lob
  private String et;

  @Lob
  private String fi;

  @Lob
  private String fr;

  @Lob
  private String ga;

  @Lob
  private String hr;

  @Lob
  private String hu;

  @Lob
  private String it;

  @Lob
  private String lt;

  @Lob
  private String lv;

  @Lob
  private String mt;

  @Lob
  private String nl;

  @Lob
  private String no;

  @Lob
  private String pl;

  @Lob
  private String pt;

  @Lob
  private String ro;

  @Lob
  private String sk;

  @Lob
  private String sl;

  @Lob
  private String sv;

  public DcatThemes() {
    // TODO Auto-generated constructor stub
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getBg() {
    return this.bg;
  }

  public void setBg(String bg) {
    this.bg = bg;
  }

  public String getCs() {
    return this.cs;
  }

  public void setCs(String cs) {
    this.cs = cs;
  }

  public String getDa() {
    return this.da;
  }

  public void setDa(String da) {
    this.da = da;
  }

  public String getDe() {
    return this.de;
  }

  public void setDe(String de) {
    this.de = de;
  }

  public String getEl() {
    return this.el;
  }

  public void setEl(String el) {
    this.el = el;
  }

  public String getEn() {
    return this.en;
  }

  public void setEn(String en) {
    this.en = en;
  }

  public String getEs() {
    return this.es;
  }

  public void setEs(String es) {
    this.es = es;
  }

  public String getEt() {
    return this.et;
  }

  public void setEt(String et) {
    this.et = et;
  }

  public String getFi() {
    return this.fi;
  }

  public void setFi(String fi) {
    this.fi = fi;
  }

  public String getFr() {
    return this.fr;
  }

  public void setFr(String fr) {
    this.fr = fr;
  }

  public String getGa() {
    return this.ga;
  }

  public void setGa(String ga) {
    this.ga = ga;
  }

  public String getHr() {
    return this.hr;
  }

  public void setHr(String hr) {
    this.hr = hr;
  }

  public String getHu() {
    return this.hu;
  }

  public void setHu(String hu) {
    this.hu = hu;
  }

  public String getIt() {
    return this.it;
  }

  public void setIt(String it) {
    this.it = it;
  }

  public String getLt() {
    return this.lt;
  }

  public void setLt(String lt) {
    this.lt = lt;
  }

  public String getLv() {
    return this.lv;
  }

  public void setLv(String lv) {
    this.lv = lv;
  }

  public String getMt() {
    return this.mt;
  }

  public void setMt(String mt) {
    this.mt = mt;
  }

  public String getNl() {
    return this.nl;
  }

  public void setNl(String nl) {
    this.nl = nl;
  }

  public String getNo() {
    return this.no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public String getPl() {
    return this.pl;
  }

  public void setPl(String pl) {
    this.pl = pl;
  }

  public String getPt() {
    return this.pt;
  }

  public void setPt(String pt) {
    this.pt = pt;
  }

  public String getRo() {
    return this.ro;
  }

  public void setRo(String ro) {
    this.ro = ro;
  }

  public String getSk() {
    return this.sk;
  }

  public void setSk(String sk) {
    this.sk = sk;
  }

  public String getSl() {
    return this.sl;
  }

  public void setSl(String sl) {
    this.sl = sl;
  }

  public String getSv() {
    return this.sv;
  }

  public void setSv(String sv) {
    this.sv = sv;
  }

}
