


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_promotion_ddl.xml#1 $$Change: 1385662 $

create table csa_prm_xlate (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null
,constraint csa_prm_xlate_p primary key (translation_id,asset_version))

create index csa_prm_xlate_wsx on csa_prm_xlate (workspace_id)
create index csa_prm_xlate_cix on csa_prm_xlate (checkin_date)

create table csa_prm_prm_xlate (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_prm_prm_xlt_p primary key (promotion_id,locale,asset_version))

create index csa_prm_xlt_tr_id on csa_prm_prm_xlate (translation_id)


go
