


--      @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_content_mgmt_ddl.xml#1 $$Change: 1385662 $  

create table csa_media_content (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	link_url	varchar2(255)	null,
	alt_text	varchar2(255)	null,
	image_srcset	varchar2(1056)	null,
	image_text_1	varchar2(255)	null,
	image_text_2	varchar2(255)	null,
	image_text_3	varchar2(255)	null,
	image_text_4	varchar2(255)	null
,constraint csa_media_content_p primary key (id,asset_version));


create table csa_media_content_xlate (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	translation_id	varchar2(40)	not null,
	name	varchar2(255)	null,
	title	varchar2(255)	null,
	description	varchar2(255)	null,
	link_url	varchar2(255)	null,
	alt_text	varchar2(255)	null,
	image_text_1	varchar2(255)	null,
	image_text_2	varchar2(255)	null,
	image_text_3	varchar2(255)	null,
	image_text_4	varchar2(255)	null
,constraint csa_media_content_xlate_p primary key (translation_id,asset_version));

create index csa_media_cont_wsx on csa_media_content_xlate (workspace_id);
create index csa_media_cont_cix on csa_media_content_xlate (checkin_date);

create table csa_media_media_xlate (
	asset_version	number(19)	not null,
	id	varchar2(40)	not null,
	locale	varchar2(40)	not null,
	translation_id	varchar2(40)	not null
,constraint csa_media_media_xlate_p primary key (id,locale,asset_version));




