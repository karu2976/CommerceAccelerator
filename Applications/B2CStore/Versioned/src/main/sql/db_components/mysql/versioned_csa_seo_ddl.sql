


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_seo_ddl.xml#1 $$Change: 1385662 $

create table csa_seo_xlate (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	title	varchar(254)	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	keywords	varchar(254)	null
,constraint csa_seo_xlate_pk primary key (translation_id,asset_version));

create index csa_seo_xlate_wsx on csa_seo_xlate (workspace_id);
create index csa_seo_xlate_cix on csa_seo_xlate (checkin_date);

create table csa_seo_seo_xlate (
	asset_version	bigint	not null,
	seo_tag_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_seo_tag_xlt_pk primary key (seo_tag_id,locale,asset_version));

commit;


