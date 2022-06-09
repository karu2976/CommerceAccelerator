


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_promotion_ddl.xml#1 $$Change: 1385662 $

      alter session set NLS_LENGTH_SEMANTICS='CHAR';
    

create table csa_prm_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	translation_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint csa_prm_xlate_p primary key (translation_id,asset_version));

create index csa_prm_xlate_wsx on csa_prm_xlate (workspace_id);
create index csa_prm_xlate_cix on csa_prm_xlate (checkin_date);

create table csa_prm_prm_xlate (
	asset_version	number(19)	not null,
	promotion_id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_prm_prm_xlt_p primary key (promotion_id,locale,asset_version));

create index csa_prm_xlt_tr_id on csa_prm_prm_xlate (translation_id);

      alter session set NLS_LENGTH_SEMANTICS='BYTE';
    



